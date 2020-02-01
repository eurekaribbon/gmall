package com.atguigu.gmall.order.controller;

import com.alibaba.dubbo.config.annotation.Reference;
import com.atguigu.gmall.anotations.LoginRequire;
import com.atguigu.gmall.bean.OmsCartItem;
import com.atguigu.gmall.bean.OmsOrder;
import com.atguigu.gmall.bean.OmsOrderItem;
import com.atguigu.gmall.bean.UmsMemberReceiveAddress;
import com.atguigu.gmall.service.CartService;
import com.atguigu.gmall.service.OrderService;
import com.atguigu.gmall.service.SkuService;
import com.atguigu.gmall.service.UmemService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

/**
 * @author lvlei
 * create on 2020-01-29-12:51
 */
@Controller
public class OrderController {


    @Reference
    CartService cartService;

    @Reference
    UmemService  umemService;

    @Reference
    OrderService orderService;

    @Reference
    SkuService skuService;

    @RequestMapping("submitOrder")
    @LoginRequire(loginSuccess = true)
    public ModelAndView submitOrder(String receiveAddressId,String tradeCode,HttpServletRequest request){
        String memberId = (String)request.getAttribute("memberId");
        String nickname = (String)request.getAttribute("nickname");

        //校验订单是否重复提交
        String success = orderService.checkTradeCode(memberId,tradeCode);
        UmsMemberReceiveAddress address = umemService.getAddressById(receiveAddressId);
        if(success.equals("success")){
        //根据用户Id生成订单信息
            List<OmsCartItem> omsCartItems = cartService.cartList(memberId);
            OmsOrder omsOrder = new OmsOrder();
            omsOrder.setCreateTime(new Date());
            omsOrder.setMemberId(memberId);
            omsOrder.setOrderType(new BigDecimal("0"));//pc app
            omsOrder.setAutoConfirmDay(7);
            omsOrder.setMemberUsername(nickname);
            omsOrder.setNote("快点发货");
            omsOrder.setSourceType(new BigDecimal("0"));//订单来源
            omsOrder.setStatus(new BigDecimal("0"));//正常 秒杀

            //生成订单号
            String outTradeNo = "gmall";
            outTradeNo = outTradeNo+System.currentTimeMillis();
            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyyMMddHHmmss");
            String format = dateFormat.format(new Date());
            outTradeNo+=format;
            omsOrder.setOrderSn(outTradeNo);

            omsOrder.setReceiverProvince(address.getProvince());
            omsOrder.setReceiverCity(address.getCity());
            omsOrder.setReceiverRegion(address.getRegion());
            omsOrder.setReceiverDetailAddress(address.getDetailAddress());
            omsOrder.setReceiverName(nickname);
            omsOrder.setReceiverPhone(address.getPhoneNumber());
            omsOrder.setReceiverPostCode(address.getPostCode());


            List<OmsOrderItem> omsOrderItems = new java.util.ArrayList<>();
            for (OmsCartItem omsCartItem : omsCartItems) {
                if(omsCartItem.getIsChecked().equals("1")){
                    //验价  验库存（远程调用库存系统）
                    boolean b = skuService.checkPrice(omsCartItem.getProductSkuId(),omsCartItem.getPrice());
                    if(b){
                        OmsOrderItem omsOrderItem = new OmsOrderItem();
                        omsOrderItem.setOrderSn(outTradeNo);
                        omsOrderItem.setProductCategoryId(omsCartItem.getProductCategoryId());
                        omsOrderItem.setProductPic(omsCartItem.getProductPic());
                        omsOrderItem.setProductPrice(omsCartItem.getPrice());
                        omsOrderItem.setProductName(omsCartItem.getProductName());
                        omsOrderItem.setProductQuantity(omsCartItem.getQuantity());
                        omsOrderItem.setProductId(omsCartItem.getProductId());
                        omsOrderItems.add(omsOrderItem);
                    }else{
                        return new ModelAndView("tradeFail");
                    }
                }
            }
            omsOrder.setOmsOrderItems(omsOrderItems);
            //将订单信息写入数据库
            // 删除购物车中的数据
            orderService.save(omsOrder);
        //重定向到支付页面
            ModelAndView modelAndView = new ModelAndView("redirect:http://payment.gmall.com:8087/index");
            modelAndView.addObject("outTradeNo",outTradeNo);
            modelAndView.addObject("totalAmount");
            return  modelAndView;

        }else{
            return new ModelAndView("tradeFail");
        }
    }

    /**
     * 跳转到结算页面
     * @return
     */
    @RequestMapping("toTrade")
    @LoginRequire(loginSuccess = true)
    public String toTrade(HttpServletRequest request, ModelMap map){
        String memberId = (String)request.getAttribute("memberId");
        String nickname = (String)request.getAttribute("nickname");

        //获取用户收货地址列表信息
        List<UmsMemberReceiveAddress> receiveAddresses = umemService.getReceiveAddressByMemberId(memberId);

        //将购物车数据转换为结算页面信息
        List<OmsCartItem> omsCartItems = cartService.cartList(memberId);
        List<OmsOrderItem> omsOrderItems = new java.util.ArrayList<>();
        for (OmsCartItem omsCartItem : omsCartItems) {
            if(omsCartItem.getIsChecked().equals("1")){
                OmsOrderItem omsOrderItem = new OmsOrderItem();
                omsOrderItem.setProductName(omsCartItem.getProductName());
                omsOrderItem.setProductPic(omsCartItem.getProductPic());
                omsOrderItems.add(omsOrderItem);
            }
        }

        //生成交易码
        String tradeCode = orderService.geneTradeCode(memberId);
        map.put("tradeCode",tradeCode);
        //计算总金额
        BigDecimal totalAount = getTotalAount(omsCartItems);
        map.put("totalAmount",totalAount);
        map.put("receiveAddresses",receiveAddresses);
        map.put("omsOrderItems",omsOrderItems);
        return "trade";
    }


    /**
     * 计算商品的选中价格
     * @param omsCartItems
     * @return
     */
    private BigDecimal getTotalAount(List<OmsCartItem> omsCartItems) {
        BigDecimal bigDecimal = new BigDecimal("0");
        for (OmsCartItem cartItem : omsCartItems) {
            //计算选中商品的总价格

            if(cartItem.getIsChecked().equals("1")){
                bigDecimal = bigDecimal.add(new BigDecimal(cartItem.getTotalPrice()));
            }
        }
        return bigDecimal;
    }
}
