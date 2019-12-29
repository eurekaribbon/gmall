package com.atguigu.gmall.item.controller;

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

    @RequestMapping("index")
    public String index(ModelMap modelMap){
        modelMap.put("name","lisi");
        return "index";
    }


    @RequestMapping("{skuId}.html")
    public String item(@PathVariable("skuId")String skuId, ModelMap modelMap){

        return "item";
    }

}
