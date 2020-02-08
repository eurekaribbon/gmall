package com.atguigu.gmall.mq;

import com.atguigu.gmall.bean.PaymentInfo;
import com.atguigu.gmall.service.PaymentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jms.annotation.JmsListener;
import org.springframework.stereotype.Component;

import javax.jms.JMSException;
import javax.jms.MapMessage;
import java.math.BigDecimal;
import java.util.Date;
import java.util.Map;

/**
 * @author lvlei
 * create on 2020-02-04-19:29
 */
@Component
public class PaymentServiceMqListener {

    @Autowired
    PaymentService paymentService;

    @JmsListener(destination = "pay_check_queue",containerFactory = "jmsQueueListener")
    public void checkResult(MapMessage mapMessage) throws JMSException {
        String out_trade_no = mapMessage.getString("out_trade_no");
        String count = mapMessage.getString("count");
        int cout = Integer.parseInt(count);

        System.out.println("进行延迟检查");
        Map<String,Object> resultMap = paymentService.checkPayResult(out_trade_no);

        if(!resultMap.isEmpty()){
            String trade_status = (String)resultMap.get("trade_status");
            if("TRADE_SUCCESS".equals(trade_status)){
                System.out.println("已经支付成功，修改支付信息");
                PaymentInfo paymentInfo = new PaymentInfo();
                paymentInfo.setTotalAmount(new BigDecimal(resultMap.get("total_amount")+""));
                paymentInfo.setOrderSn(out_trade_no);
                paymentInfo.setPaymentStatus("已支付");
                paymentInfo.setAlipayTradeNo((String)resultMap.get("trade_no"));
                paymentInfo.setCallbackContent((String)resultMap.get("content"));
                paymentInfo.setCallbackTime(new Date());
                paymentService.updatePaymentInfo(paymentInfo);
                return;
            }
        }
        if(cout>0){
            System.out.println("没有支付成功，继续发送延迟检查");
            cout = --cout;
            paymentService.sendDelayCheck(out_trade_no,cout);
        }

        /*if(resultMap.isEmpty()){
            //检查失败
            System.out.println("没有支付成功，继续发送延迟检查");
            if(cout>0){
                cout = --cout;
                paymentService.sendDelayCheck(out_trade_no,cout);

            }

        }else{
            //调用成功
            String trade_status = (String)resultMap.get("trade_status");
            if("TRADE_SUCCESS".equals(trade_status)){
                System.out.println("已经支付成功，修改支付信息");

           }else{
                System.out.println("没有支付成功，继续发送延迟检查");
                if(cout>0){
                    cout = --cout;
                    paymentService.sendDelayCheck(out_trade_no,cout);

                }
            }
        }*/
    }
}
