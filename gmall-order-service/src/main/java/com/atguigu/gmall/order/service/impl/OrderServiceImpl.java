package com.atguigu.gmall.order.service.impl;

import com.alibaba.dubbo.config.annotation.Reference;
import com.alibaba.dubbo.config.annotation.Service;
import com.atguigu.gmall.bean.OmsOrder;
import com.atguigu.gmall.bean.OmsOrderItem;
import com.atguigu.gmall.order.mapper.OmsOrderItemMapper;
import com.atguigu.gmall.order.mapper.OmsOrderMapper;
import com.atguigu.gmall.service.CartService;
import com.atguigu.gmall.service.OrderService;
import com.atguigu.gmall.util.ActiveMQUtil;
import com.atguigu.gmall.util.RedisUtil;
import org.apache.activemq.command.ActiveMQMapMessage;
import org.springframework.beans.factory.annotation.Autowired;
import redis.clients.jedis.Jedis;
import tk.mybatis.mapper.entity.Example;

import javax.jms.*;
import java.math.BigDecimal;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

/**
 * @author lvlei
 * create on 2020-01-30-11:26
 */
@Service
public class OrderServiceImpl implements OrderService {

    @Autowired
    RedisUtil redisUtil;

    @Autowired
    OmsOrderMapper omsOrderMapper;

    @Autowired
    OmsOrderItemMapper omsOrderItemMapper;

    @Reference
    CartService cartService;

    @Autowired
    ActiveMQUtil activeMQUtil;

    /**
     * 校验交易码
     * @param memberId
     * @param tradeCode
     * @return
     */
    @Override
    public String checkTradeCode(String memberId, String tradeCode) {
        String tradeKey = "user:"+memberId+":tradeCode";
        Jedis jedis = null;
        try {
            jedis = redisUtil.getJedis();
            String code = jedis.get(tradeKey);
            /*if(StringUtils.isNotBlank(code)&&tradeCode.equals(code)){
                //删除key
                jedis.del(tradeKey);//采用lua脚本防止并发情况重复提交情况
                return "success";
            }*/

            //对比防重删令牌
            String script = "if redis.call('get', KEYS[1]) == ARGV[1] then return redis.call('del', KEYS[1]) else return 0 end";
            Long eval = (Long) jedis.eval(script, Collections.singletonList(tradeKey), Collections.singletonList(code));//找到即删除
            if(eval!=null&&eval!=0){
                return "success";
            }else{
                return "fail";
            }
        } finally {
            jedis.close();
        }
    }


    /**
     * 生成交易码
     * @param memberId
     * @return
     */
    @Override
    public String geneTradeCode(String memberId) {
        Jedis jedis = redisUtil.getJedis();

        String tradeKey = "user:"+memberId+":tradeCode";

        String tradeCode = UUID.randomUUID().toString();

        jedis.setex(tradeKey,60*15,tradeCode);

        jedis.close();

        return tradeCode;
    }

    @Override
    public void save(OmsOrder omsOrder) {
        int i = omsOrderMapper.insertSelective(omsOrder);
        String orderId = omsOrder.getId();
        List<OmsOrderItem> omsOrderItems = omsOrder.getOmsOrderItems();
        for (OmsOrderItem omsOrderItem : omsOrderItems) {
            omsOrderItem.setOrderId(orderId);
            omsOrderItemMapper.insertSelective(omsOrderItem);
            //删除购物车数据
            //cartService.delCart("根据购物车的主键删除");
        }
    }

    @Override
    public OmsOrder getOrderByOutTradeNo(String outTradeNo) {
        OmsOrder omsOrder = new OmsOrder();
        omsOrder.setOrderSn(outTradeNo);
        return omsOrderMapper.selectOne(omsOrder);
    }

    @Override
    public void updateStatus(String out_trade_no) {
        Example example = new Example(OmsOrder.class);
        example.createCriteria().andEqualTo("orderSn",out_trade_no);
        OmsOrder omsOrder = new OmsOrder();
        omsOrder.setStatus(new BigDecimal("1"));
        omsOrderMapper.updateByExampleSelective(omsOrder,example);

        //发送订单已支付
        Connection connection = null;
        Session session = null;
        try{
            connection = activeMQUtil.getConnectionFactory().createConnection();
            session = connection.createSession(true, Session.SESSION_TRANSACTED);

            Queue order_pay_quequ = session.createQueue("order_pay_quequ");
            MessageProducer producer = session.createProducer(order_pay_quequ);

            ActiveMQMapMessage activeMQMapMessage = new ActiveMQMapMessage();

            producer.send(activeMQMapMessage);

            session.commit();

        }catch(Exception exception){
            try {
                session.rollback();
            } catch (JMSException e) {
                e.printStackTrace();
            }
        }
    }
}
