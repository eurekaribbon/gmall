package com.atguigu.gmall.user.service.impl;


import com.alibaba.dubbo.config.annotation.Service;
import com.atguigu.gmall.bean.UmsMember;
import com.atguigu.gmall.bean.UmsMemberReceiveAddress;
import com.atguigu.gmall.service.UmemService;
import com.atguigu.gmall.user.mapper.UmsMemberMapper;
import com.atguigu.gmall.user.mapper.UmsMemberReceiveAddressMapper;
import org.springframework.beans.factory.annotation.Autowired;

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
}
