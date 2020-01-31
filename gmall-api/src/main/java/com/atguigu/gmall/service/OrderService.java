package com.atguigu.gmall.service;

import com.atguigu.gmall.bean.OmsOrder;

/**
 * @author lvlei
 * create on 2020-01-30-11:24
 */
public interface OrderService {
    String checkTradeCode(String memberId, String tradeCode);

    String geneTradeCode(String memberId);

    void save(OmsOrder omsOrder);
}
