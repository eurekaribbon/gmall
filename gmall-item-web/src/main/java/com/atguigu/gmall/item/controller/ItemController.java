package com.atguigu.gmall.item.controller;

import com.alibaba.dubbo.config.annotation.Reference;
import com.atguigu.gmall.bean.PmsSkuInfo;
import com.atguigu.gmall.service.SkuService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * @author lvlei
 * create on 2019-12-29-14:04
 */
@Controller
public class ItemController {

    @Reference
    SkuService skuService;

    @RequestMapping("index")
    public String index(ModelMap modelMap){
        modelMap.put("name","lisi");
        return "index";
    }


    @RequestMapping("{skuId}.html")
    public String item(@PathVariable("skuId")String skuId, ModelMap modelMap){
        PmsSkuInfo pmsSkuInfo = skuService.getSkuInfoBySkuId(skuId);
        modelMap.put("skuInfo",pmsSkuInfo);
        return "item";
    }

}
