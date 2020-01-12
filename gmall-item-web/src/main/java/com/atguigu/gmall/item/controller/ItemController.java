package com.atguigu.gmall.item.controller;

import com.alibaba.dubbo.config.annotation.Reference;
import com.alibaba.fastjson.JSON;
import com.atguigu.gmall.bean.PmsProductSaleAttr;
import com.atguigu.gmall.bean.PmsSkuInfo;
import com.atguigu.gmall.bean.PmsSkuSaleAttrValue;
import com.atguigu.gmall.service.PmsBaseAttrService;
import com.atguigu.gmall.service.SkuService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author lvlei
 * create on 2019-12-29-14:04
 */
@Controller
public class ItemController {

    @Reference
    SkuService skuService;
    @Reference
    PmsBaseAttrService pmsBaseAttrService;


    @RequestMapping("index")
    public String index(ModelMap modelMap){
        modelMap.put("name","lisi");
        return "index";
    }


    @RequestMapping("{skuId}.html")
    public String item(@PathVariable("skuId")String skuId, ModelMap modelMap){
        PmsSkuInfo pmsSkuInfo = skuService.getSkuInfoBySkuId(skuId);

        //查找属性列表
        String spuId = pmsSkuInfo.getSpuId();
        //List<PmsProductSaleAttr> pmsProductSaleAttrs = pmsBaseAttrService.spuSaleAttrList(spuId);
        List<PmsProductSaleAttr> pmsProductSaleAttrs  = pmsBaseAttrService.spuSaleAttrListCheckBySku(spuId,pmsSkuInfo.getId());

        //生成hashkey
        List<PmsSkuInfo> pmsSkuInfos = skuService.getSkuInfoBySpuId(pmsSkuInfo.getSpuId());
        Map<String,String> map = new HashMap<>();
        for (PmsSkuInfo skuInfo : pmsSkuInfos) {
            String k = "";
            String v = skuInfo.getId();
            List<PmsSkuSaleAttrValue> skuSaleAttrValueList = skuInfo.getSkuSaleAttrValueList();
            for (PmsSkuSaleAttrValue saleAttrValue : skuSaleAttrValueList) {
                k+=saleAttrValue.getSaleAttrValueId()+"|";
            }
            map.put(k,v);
        }

        modelMap.put("skuInfo",pmsSkuInfo);
        modelMap.put("spuSaleAttrListCheckBySku",pmsProductSaleAttrs);
        modelMap.put("hashKey", JSON.toJSONString(map));
        return "item";
    }

}
