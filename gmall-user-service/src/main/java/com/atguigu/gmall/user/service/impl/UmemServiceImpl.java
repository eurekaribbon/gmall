package com.atguigu.gmall.user.service.impl;


import com.alibaba.dubbo.config.annotation.Service;
import com.alibaba.fastjson.JSON;
import com.atguigu.gmall.bean.UmsMember;
import com.atguigu.gmall.bean.UmsMemberReceiveAddress;
import com.atguigu.gmall.service.UmemService;
import com.atguigu.gmall.user.mapper.UmsMemberMapper;
import com.atguigu.gmall.user.mapper.UmsMemberReceiveAddressMapper;
import com.atguigu.gmall.util.RedisUtil;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import redis.clients.jedis.Jedis;

import java.util.List;

/**
 * @author lvlei
 * create on 2019-12-22-12:46
 */

@Service
public class UmemServiceImpl implements UmemService {

    @Autowired
    UmsMemberMapper umsMemberMapper;

    @Autowired
    UmsMemberReceiveAddressMapper umsMemberReceiveAddressMapper;

    @Autowired
    RedisUtil redisUtil;

    @Override
    public List<UmsMember> getAllUser() {

        List<UmsMember> memberList = umsMemberMapper.selectAll();
        return memberList;
    }

    @Override
    public List<UmsMemberReceiveAddress> getReceiveAddressByMemberId(String memberId) {
        UmsMemberReceiveAddress address = new UmsMemberReceiveAddress();
        address.setMemberId(memberId);
        List<UmsMemberReceiveAddress> addresses = umsMemberReceiveAddressMapper.select(address);
        return addresses;
    }

    @Override
    public UmsMember login(UmsMember umsMember) {
        //先查询缓存
        Jedis jedis = null;
        UmsMember umsMember1 = null;
        try {
            jedis = redisUtil.getJedis();
            if(jedis!=null){
                String s = jedis.get("user:" + umsMember.getPassword() + ":info");
                if(StringUtils.isNotBlank(s)){
                    umsMember1 = JSON.parseObject(s, UmsMember.class);
                }else{
                    //缓存中没有  查询db  查找到放入缓存 返回
                    umsMember1 = loginFromDb(umsMember);
                    if(umsMember!=null){
                        jedis.setex("user:" + umsMember.getPassword() + ":info",60*60*24, JSON.toJSONString(umsMember1));
                    }
                }
            }else{
                //连接缓存失败  查询db
                umsMember1 = loginFromDb(umsMember);
                if(umsMember!=null){
                    jedis.setex("user:" + umsMember.getPassword() + ":info",60*60*24, JSON.toJSONString(umsMember1));
                }
            }
        } finally {
            jedis.close();
        }
        return umsMember1;
    }

    @Override
    public void addToken(String token, String memberId) {
        Jedis jedis = redisUtil.getJedis();
        jedis.setex("user:"+memberId+":token",60*60*2,token);
        jedis.close();
    }

    @Override
    public UmsMember addOauthUser(UmsMember umsMember) {
        int i = umsMemberMapper.insert(umsMember);
        return umsMember;
    }

    @Override
    public UmsMember umsMembercheck(UmsMember umsMember1) {
        return umsMemberMapper.selectOne(umsMember1);
    }

    @Override
    public UmsMemberReceiveAddress getAddressById(String receiveAddressId) {
        UmsMemberReceiveAddress address = new UmsMemberReceiveAddress();
        address.setId(receiveAddressId);
        return umsMemberReceiveAddressMapper.selectOne(address);
    }

    private UmsMember loginFromDb(UmsMember umsMember) {
        UmsMember umsMember1 = umsMemberMapper.selectOne(umsMember);
        return umsMember1;
    }
}
