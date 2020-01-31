package com.atguigu.gmall.service;


import com.atguigu.gmall.bean.UmsMember;
import com.atguigu.gmall.bean.UmsMemberReceiveAddress;

import java.util.List;

public interface UmemService {
    List<UmsMember> getAllUser();

    List<UmsMemberReceiveAddress> getReceiveAddressByMemberId(String memberId);

    UmsMember login(UmsMember umsMember);

    void addToken(String token, String memberId);

    UmsMember addOauthUser(UmsMember umsMember);

    UmsMember umsMembercheck(UmsMember umsMember1);

    UmsMemberReceiveAddress getAddressById(String receiveAddressId);
}
