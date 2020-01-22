package com.atguigu.gmall.cart.service.impl;

import com.alibaba.dubbo.config.annotation.Service;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.atguigu.gmall.bean.OmsCartItem;
import com.atguigu.gmall.cart.mapper.OmsCartItemMapper;
import com.atguigu.gmall.service.CartService;
import com.atguigu.gmall.util.RedisUtil;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import redis.clients.jedis.Jedis;
import tk.mybatis.mapper.entity.Example;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class CartServiceImpl implements CartService {

    @Autowired
    OmsCartItemMapper omsCartItemMapper;

    @Autowired
    RedisUtil redisUtil;

    @Override
    public OmsCartItem checkIfExist(String memberId, String skuId) {
        OmsCartItem omsCartItem = new OmsCartItem();
        omsCartItem.setMemberId(memberId);
        omsCartItem.setProductSkuId(skuId);
        OmsCartItem cartItem = omsCartItemMapper.selectOne(omsCartItem);
        return cartItem;
    }

    @Override
    public void updateCart(OmsCartItem omsCartItemFromDb) {
        Example example = new Example(OmsCartItem.class);
        example.createCriteria().andEqualTo("id",omsCartItemFromDb.getId());
        omsCartItemMapper.updateByExampleSelective(omsCartItemFromDb,example);
    }

    @Override
    public void addCart(OmsCartItem omsCartItem) {
        String memberId = omsCartItem.getMemberId();
        if(StringUtils.isNotBlank(memberId)){
            omsCartItemMapper.insert(omsCartItem);
        }
    }

    @Override
    public void flushCartCache(String memberId) {
        Jedis jedis = redisUtil.getJedis();

        OmsCartItem omsCartItem = new OmsCartItem();
        omsCartItem.setMemberId(memberId);
        List<OmsCartItem> omsCartItems = omsCartItemMapper.select(omsCartItem);
        Map<String,String> map = new HashMap<>();
        for (OmsCartItem cartItem : omsCartItems) {
            //计算总价格

            map.put(cartItem.getProductSkuId(), JSON.toJSONString(cartItem));
        }

        //更新缓存
        String key = "user:"+memberId+":cart";
        //删除key
        jedis.del(key);

        jedis.hmset(key,map);
        jedis.close();

    }

    @Override
    public List<OmsCartItem> cartList(String memberId) {
        Jedis jedis = redisUtil.getJedis();
        List<OmsCartItem> omsCartItems = new java.util.ArrayList<>();
        List<String> hvals = jedis.hvals("user:" + memberId + ":cart");
        if(hvals!=null){
            for (String hval : hvals) {
                OmsCartItem omsCartItem = JSONObject.parseObject(hval, OmsCartItem.class);
                omsCartItems.add(omsCartItem);
            }
        }
        return omsCartItems;
    }
}
