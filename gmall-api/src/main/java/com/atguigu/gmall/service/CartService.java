package com.atguigu.gmall.service;

import com.atguigu.gmall.bean.OmsCartItem;

public interface CartService {
    OmsCartItem checkIfExist(String memberId, String skuId);

    void updateCart(OmsCartItem omsCartItemFromDb);

    void addCart(OmsCartItem omsCartItem);

    void flushCartCache(String memberId);
}
