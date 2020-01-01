package com.atguigu.gmall.manage.controller;


import com.alibaba.dubbo.config.annotation.Reference;
import com.atguigu.gmall.bean.PmsProductImage;
import com.atguigu.gmall.bean.PmsProductInfo;
import com.atguigu.gmall.manage.util.PmsUploadUtil;
import com.atguigu.gmall.service.PmsProductService;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;

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

    @RequestMapping("fileUpload")
    @ResponseBody
    public String fileUpload(MultipartFile file){
        String url = PmsUploadUtil.uploadImage(file);
        System.out.println(url);
        return url;
    }

    @RequestMapping("saveSpuInfo")
    @ResponseBody
    public String saveSpuInfo(@RequestBody PmsProductInfo pmsProductInfo){
        pmsProductService.saveSpuInfo(pmsProductInfo);
        return "success";
    }

    @RequestMapping("spuImageList")
    @ResponseBody
    public List<PmsProductImage> spuImageList(String spuId){
        return pmsProductService.spuImageList(spuId);
    }

}
