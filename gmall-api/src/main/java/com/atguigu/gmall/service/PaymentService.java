package com.atguigu.gmall.service;

import com.atguigu.gmall.bean.PaymentInfo;

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
}
