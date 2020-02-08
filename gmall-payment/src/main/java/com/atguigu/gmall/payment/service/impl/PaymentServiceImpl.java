package com.atguigu.gmall.payment.service.impl;

import com.alibaba.dubbo.config.annotation.Service;
import com.alibaba.fastjson.JSON;
import com.alipay.api.AlipayApiException;
import com.alipay.api.AlipayClient;
import com.alipay.api.request.AlipayTradeQueryRequest;
import com.alipay.api.response.AlipayTradeQueryResponse;
import com.atguigu.gmall.bean.PaymentInfo;
import com.atguigu.gmall.payment.mapper.PaymentInfoMapper;
import com.atguigu.gmall.service.PaymentService;
import com.atguigu.gmall.util.ActiveMQUtil;
import org.apache.activemq.ScheduledMessage;
import org.apache.activemq.command.ActiveMQMapMessage;
import org.springframework.beans.factory.annotation.Autowired;
import tk.mybatis.mapper.entity.Example;

import javax.jms.*;
import java.util.HashMap;
import java.util.Map;

/**
 * @author lvlei
 * create on 2020-02-01-15:09
 */
@Service
public class PaymentServiceImpl implements PaymentService {

    @Autowired
    PaymentInfoMapper paymentInfoMapper;

    @Autowired
    ActiveMQUtil activeMQUtil;

    @Autowired
    AlipayClient alipayClient;

    @Override
    public void save(PaymentInfo paymentInfo) {
        paymentInfoMapper.insertSelective(paymentInfo);
    }

    @Override
    public void updatePaymentInfo(PaymentInfo paymentInfo) {

        //做幂等性检查
        PaymentInfo paymentInfoParam = new PaymentInfo();
        paymentInfoParam.setOrderSn(paymentInfo.getOrderSn());
        PaymentInfo paymentInfoCheck = paymentInfoMapper.selectOne(paymentInfoParam);

        if(paymentInfoCheck.getPaymentStatus().equals("已支付")){
            return;
        }
        Example example = new Example(PaymentInfo.class);
        example.createCriteria().andEqualTo("orderSn",paymentInfo.getOrderSn());

        //发送消息
        ConnectionFactory connectionFactory = activeMQUtil.getConnectionFactory();
        Session session = null;
        try {
            Connection connection = connectionFactory.createConnection();
            session = connection.createSession(true, Session.SESSION_TRANSACTED);
            Queue queue = session.createQueue("payment_success_queue");
            MessageProducer producer = session.createProducer(queue);

            ActiveMQMapMessage mapMessage = new ActiveMQMapMessage();
            mapMessage.setString("out_trade_no",paymentInfo.getOrderSn());

            paymentInfoMapper.updateByExampleSelective(paymentInfo,example);
            producer.send(mapMessage);

            session.commit();
        } catch (JMSException e) {
            try {
                session.rollback();
            } catch (JMSException e1) {
                e1.printStackTrace();
            }
            e.printStackTrace();
        }
    }

    @Override
    public void sendDelayCheck(String outTradeNo,int count) {
        Connection connection = null;
        Session session = null;
        try{
            connection = activeMQUtil.getConnectionFactory().createConnection();
            session = connection.createSession(true, Session.SESSION_TRANSACTED);

            Queue payCheckQueue = session.createQueue("pay_check_queue");
            MessageProducer producer = session.createProducer(payCheckQueue);

            ActiveMQMapMessage activeMQMapMessage = new ActiveMQMapMessage();
            activeMQMapMessage.setString("out_trade_no",outTradeNo);
            activeMQMapMessage.setString("count",count+"");
            //设置延迟时间
            activeMQMapMessage.setLongProperty(ScheduledMessage.AMQ_SCHEDULED_DELAY,30*1000);

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

    @Override
    public Map<String, Object> checkPayResult(String out_trade_no) {

        AlipayTradeQueryRequest request = new AlipayTradeQueryRequest();
        Map<String,String> paramMap = new HashMap<>();
        paramMap.put("out_trade_no",out_trade_no);
        request.setBizContent(JSON.toJSONString(paramMap));

        AlipayTradeQueryResponse response = null;
        Map<String,Object> resultMap = new HashMap<>();
        try {
            response = alipayClient.execute(request);
        } catch (AlipayApiException e) {
            e.printStackTrace();
        }
        if(response.isSuccess()){
            System.out.println("调用成功");
            resultMap.put("trade_no",response.getTradeNo());
            resultMap.put("trade_status",response.getTradeStatus());
            resultMap.put("total_amount",response.getTotalAmount());
            resultMap.put("out_trade_no",response.getOutTradeNo());
            resultMap.put("content",response.getMsg());
        } else {
            System.out.println("调用失败");
        }
        return resultMap;
    }
}
