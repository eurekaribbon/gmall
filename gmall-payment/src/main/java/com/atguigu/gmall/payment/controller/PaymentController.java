package com.atguigu.gmall.payment.controller;

import com.alibaba.dubbo.config.annotation.Reference;
import com.alibaba.fastjson.JSON;
import com.alipay.api.AlipayApiException;
import com.alipay.api.AlipayClient;
import com.alipay.api.request.AlipayTradePagePayRequest;
import com.atguigu.gmall.anotations.LoginRequire;
import com.atguigu.gmall.bean.OmsOrder;
import com.atguigu.gmall.bean.PaymentInfo;
import com.atguigu.gmall.config.AlipayConfig;
import com.atguigu.gmall.service.OrderService;
import com.atguigu.gmall.service.PaymentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import java.math.BigDecimal;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * @author lvlei
 * create on 2020-01-31-16:02
 */
@Controller
public class PaymentController {

    @Autowired
    AlipayClient alipayClient;

    @Autowired
    PaymentService paymentService;

    @Reference
    OrderService orderService;

    /**
     * 支付宝同步回调地址
     * @return
     */
    @RequestMapping("alipay/callback/return")
    @LoginRequire(loginSuccess = true)
    public String callBackReturn(HttpServletRequest request){

        //获取回调信息
        String queryString = request.getQueryString();
        String trade_no = request.getParameter("trade_no");//支付交易凭证号
        String total_amount = request.getParameter("total_amount");
        String sign = request.getParameter("sign");
        String out_trade_no = request.getParameter("out_trade_no");
        //更新支付信息
        PaymentInfo paymentInfo = new PaymentInfo();
        paymentInfo.setTotalAmount(new BigDecimal(total_amount));
        paymentInfo.setOrderSn(out_trade_no);
        paymentInfo.setPaymentStatus("已支付");
        paymentInfo.setAlipayTradeNo(trade_no);
        paymentInfo.setCallbackContent(queryString);
        paymentInfo.setCallbackTime(new Date());
        paymentService.updatePaymentInfo(paymentInfo);
        return "finish";
    }

    @RequestMapping("wx/submit")
    @LoginRequire(loginSuccess = true)
    public String mx(String outTradeNo, HttpServletRequest request, ModelMap map){
        return null;
    }

    @RequestMapping("alipay/submit")
    @LoginRequire(loginSuccess = true)
    @ResponseBody
    public String alipay(String outTradeNo, HttpServletRequest request, ModelMap map){
        String form = "";
        AlipayTradePagePayRequest payRequest = new AlipayTradePagePayRequest();
        //回调地址
        payRequest.setReturnUrl(AlipayConfig.return_payment_url);
        payRequest.setNotifyUrl(AlipayConfig.notify_payment_url);
        //请求参数
        Map<String,Object> paramMap = new HashMap<>();
        paramMap.put("out_trade_no",outTradeNo);
        paramMap.put("product_code","FAST_INSTANT_TRADE_PAY");
        paramMap.put("total_amount",0.01);
        paramMap.put("subject","华为手机");
        String params = JSON.toJSONString(paramMap);
        payRequest.setBizContent(params);
        try {
            form = alipayClient.pageExecute(payRequest).getBody();
            System.out.println(form);
        } catch (AlipayApiException e) {
            e.printStackTrace();
        }
        //生成并且保存用户的支付信息
        OmsOrder omsOrder = orderService.getOrderByOutTradeNo(outTradeNo);
        PaymentInfo paymentInfo = new PaymentInfo();
        paymentInfo.setCreateTime(new Date());
        paymentInfo.setOrderId(omsOrder.getId());
        paymentInfo.setPaymentStatus("未支付");
        paymentInfo.setOrderSn(outTradeNo);
        paymentService.save(paymentInfo);
        //提交请求到支付宝
        return form;
    }


    @RequestMapping("index")
    @LoginRequire(loginSuccess = true)
    public String index(String outTradeNo, HttpServletRequest request, ModelMap map){
        map.put("totalAmount",0.01);
        map.put("outTradeNo",outTradeNo);
        return "index";
    }
}
