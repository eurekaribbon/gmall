package com.atguigu.gmall.order.mq;

import com.atguigu.gmall.service.OrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jms.annotation.JmsListener;
import org.springframework.stereotype.Component;

import javax.jms.JMSException;
import javax.jms.MapMessage;

/**
 * @author lvlei
 * create on 2020-02-04-12:16
 */
@Component
public class OrderServiceMqListener {

    @Autowired
    OrderService orderService;

    @JmsListener(destination = "payment_success_queue",containerFactory = "jmsQueueListener")
    public void updateOrderStatus(MapMessage mapMessage) throws JMSException {
        String out_trade_no = mapMessage.getString("out_trade_no");
        System.out.println(out_trade_no);

        orderService.updateStatus(out_trade_no);
    }
}
