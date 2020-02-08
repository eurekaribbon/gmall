package com.atguigu.gmall.service;

import com.atguigu.gmall.bean.PaymentInfo;

import java.util.Map;

public interface PaymentService {
    /**
     * 保存支付信息
     * @param paymentInfo
     */
    void save(PaymentInfo paymentInfo);

    /**
     * 更新支付订单信息
     * @param paymentInfo
     */
    void updatePaymentInfo(PaymentInfo paymentInfo);

    /**
     * 发送延时检查消息
     * @param outTradeNo
     */
    void sendDelayCheck(String outTradeNo,int count);

    /**
     * 调用支付宝接口检查支付结果
     * @param out_trade_no
     * @return
     */
    Map<String, Object> checkPayResult(String out_trade_no);
}
