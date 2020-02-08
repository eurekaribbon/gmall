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

    OmsOrder getOrderByOutTradeNo(String outTradeNo);

    /**
     * 支付成功后更新订单状态
     * @param out_trade_no
     */
    void updateStatus(String out_trade_no);
}
