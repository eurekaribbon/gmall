package com.atguigu.gmall.manage.service.impl;

import com.alibaba.dubbo.config.annotation.Service;
import com.atguigu.gmall.bean.PmsSkuAttrValue;
import com.atguigu.gmall.bean.PmsSkuImage;
import com.atguigu.gmall.bean.PmsSkuInfo;
import com.atguigu.gmall.bean.PmsSkuSaleAttrValue;
import com.atguigu.gmall.manage.mapper.PmsSkuAttrValueMapper;
import com.atguigu.gmall.manage.mapper.PmsSkuImageMapper;
import com.atguigu.gmall.manage.mapper.PmsSkuInfoMapper;
import com.atguigu.gmall.manage.mapper.PmsSkuSaleAttrValueMapper;
import com.atguigu.gmall.service.SkuService;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

/**
 * @author lvlei
 * create on 2020-01-01-22:06
 */

@Service
public class SkuServiceImpl implements SkuService {
    @Autowired
    PmsSkuInfoMapper pmsSkuInfoMapper;

    @Autowired
    PmsSkuImageMapper pmsSkuImageMapper;

    @Autowired
    PmsSkuAttrValueMapper pmsSkuAttrValueMapper;

    @Autowired
    PmsSkuSaleAttrValueMapper pmsSkuSaleAttrValueMapper;


    @Override
    public void saveSkuInfo(PmsSkuInfo pmsSkuInfo) {
        int i = pmsSkuInfoMapper.insert(pmsSkuInfo);
        if(i>0){
            String skuInfoId = pmsSkuInfo.getId();
            List<PmsSkuImage> skuImageList = pmsSkuInfo.getSkuImageList();
            for (PmsSkuImage skuImage : skuImageList) {
                //保存图片
                skuImage.setSkuId(skuInfoId);
                pmsSkuImageMapper.insert(skuImage);
            }

            List<PmsSkuAttrValue> skuAttrValueList = pmsSkuInfo.getSkuAttrValueList();
            for (PmsSkuAttrValue pmsSkuAttrValue : skuAttrValueList) {
                //保存平台属性
                pmsSkuAttrValue.setSkuId(skuInfoId);
                pmsSkuAttrValueMapper.insert(pmsSkuAttrValue);
            }

            List<PmsSkuSaleAttrValue> skuSaleAttrValueList = pmsSkuInfo.getSkuSaleAttrValueList();
            for (PmsSkuSaleAttrValue pmsSkuSaleAttrValue : skuSaleAttrValueList) {
                //保存商品sku销售属性
                pmsSkuSaleAttrValue.setSkuId(skuInfoId);
                pmsSkuSaleAttrValueMapper.insert(pmsSkuSaleAttrValue);
            }
        }

    }

    @Override
    public PmsSkuInfo getSkuInfoBySkuId(String skuId) {
        PmsSkuInfo pmsSkuInfo = new PmsSkuInfo();
        pmsSkuInfo.setId(skuId);
        PmsSkuInfo skuInfo = pmsSkuInfoMapper.selectOne(pmsSkuInfo);
        PmsSkuImage pmsSkuImage = new PmsSkuImage();
        pmsSkuImage.setSkuId(skuId);
        List<PmsSkuImage> skuImageList = pmsSkuImageMapper.select(pmsSkuImage);
        skuInfo.setSkuImageList(skuImageList);
        return skuInfo;
    }
}
