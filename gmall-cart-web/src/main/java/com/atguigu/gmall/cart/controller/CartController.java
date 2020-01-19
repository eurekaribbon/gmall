package com.atguigu.gmall.cart.controller;

import com.alibaba.dubbo.config.annotation.Reference;
import com.alibaba.fastjson.JSONObject;
import com.atguigu.gmall.bean.OmsCartItem;
import com.atguigu.gmall.bean.PmsSkuInfo;
import com.atguigu.gmall.service.SkuService;
import com.atguigu.gmall.util.CookieUtil;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Date;
import java.util.List;

/**
 * @author lvlei
 * create on 2020-01-19-21:59
 */
@Controller
public class CartController {

    @Reference
    SkuService skuService;

    @RequestMapping("addToCart")
    public String addToCart(String skuId, Integer quantity, HttpServletRequest request, HttpServletResponse response){

        //调用商品服务查询商品信息
        PmsSkuInfo pmsSkuInfo = skuService.getSkuInfoBySkuId(skuId);

        //将商品信息封装成购物车信息
        OmsCartItem omsCartItem = new OmsCartItem();
        omsCartItem.setCreateDate(new Date());
        omsCartItem.setDeleteStatus(0);
        omsCartItem.setPrice(pmsSkuInfo.getPrice());
        omsCartItem.setProductCategoryId(pmsSkuInfo.getCatalog3Id());
        omsCartItem.setProductId("");
        omsCartItem.setProductPic(pmsSkuInfo.getSkuDefaultImg());
        omsCartItem.setQuantity((quantity));
        omsCartItem.setProductSkuId(skuId);
        omsCartItem.setProductName(pmsSkuInfo.getSkuName());

        //判断用户是否登录
        String memberId = "1";

        //根据用户是否登录 走cookie db
        if(StringUtils.isNotBlank("")){
            //用户已经登陆

        }else{
            //用户未登录
            String cartListDbJson = CookieUtil.getCookieValue(request, "cartListDb", true);
            List<OmsCartItem> omsCartItems =  new java.util.ArrayList<>();
            if(StringUtils.isBlank(cartListDbJson)){
                //购物车为空
                omsCartItems.add(omsCartItem);
            }else{
                omsCartItems = JSONObject.parseArray(cartListDbJson, OmsCartItem.class);

                Boolean b = if_cart_exist(omsCartItem,omsCartItems);
                if(b){
                    //购物车数据已存在
                    for (OmsCartItem cartItem : omsCartItems) {
                        if(cartItem.getProductSkuId().equals(omsCartItem.getProductSkuId())){
                            cartItem.setQuantity(cartItem.getQuantity()+1);
                        }
                    }
                }else{
                    //购物车数据不存在
                    omsCartItems.add(omsCartItem);
                }
            }
            CookieUtil.setCookie(request,response,"cartListDb", JSONObject.toJSONString(omsCartItems),60*60*2,true);
        }

        return "redirect:/success.html";
    }

    private Boolean if_cart_exist(OmsCartItem omsCartItem, List<OmsCartItem> omsCartItems1) {
        boolean b = false;
        for (OmsCartItem cartItem : omsCartItems1) {
            if(cartItem.getProductSkuId().equals(omsCartItem.getProductSkuId())){
                b = true;
            }
        }
        return b;
    }

}
