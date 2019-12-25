package com.atguigu.gmall.manage.controller;


import com.alibaba.dubbo.config.annotation.Reference;
import com.atguigu.gmall.bean.PmsProductInfo;
import com.atguigu.gmall.service.PmsProductService;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.List;

@Controller
@CrossOrigin
public class PmsProductController {

    @Reference
    PmsProductService pmsProductService;

    @RequestMapping("spuList")
    @ResponseBody
    public List<PmsProductInfo> getSpuList(String catalog3Id){
        return pmsProductService.getSpuList(catalog3Id);
    }
}
