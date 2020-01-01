package com.atguigu.gmall.manage.service.impl;

import com.alibaba.dubbo.config.annotation.Service;
import com.atguigu.gmall.bean.PmsProductImage;
import com.atguigu.gmall.bean.PmsProductInfo;
import com.atguigu.gmall.bean.PmsProductSaleAttr;
import com.atguigu.gmall.bean.PmsProductSaleAttrValue;
import com.atguigu.gmall.manage.mapper.PmsProductImageMapper;
import com.atguigu.gmall.manage.mapper.PmsProductMapper;
import com.atguigu.gmall.manage.mapper.PmsProductSaleAttrMapper;
import com.atguigu.gmall.manage.mapper.PmsProductSaleAttrValueMapper;
import com.atguigu.gmall.service.PmsProductService;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

@Service
public class PmsProductServiceImpl implements PmsProductService {

    @Autowired
    PmsProductMapper pmsProductMapper;

    @Autowired
    PmsProductSaleAttrMapper pmsProductSaleAttrMapper;

    @Autowired
    PmsProductSaleAttrValueMapper pmsProductSaleAttrValueMapper;

    @Autowired
    PmsProductImageMapper pmsProductImageMapper;

    @Override
    public List<PmsProductInfo> getSpuList(String catalog3Id) {
        PmsProductInfo pmsProductInfo = new PmsProductInfo();
        pmsProductInfo.setCatalog3Id(catalog3Id);
        List<PmsProductInfo> pmsProductInfos = pmsProductMapper.select(pmsProductInfo);
        return pmsProductInfos;
    }

    @Override
    public void saveSpuInfo(PmsProductInfo pmsProductInfo) {
        int i = pmsProductMapper.insert(pmsProductInfo);
        if(i>=1){
            String proId = pmsProductInfo.getId();

            //保存商品销售属性
            List<PmsProductSaleAttr> spuSaleAttrList = pmsProductInfo.getSpuSaleAttrList();
            if (spuSaleAttrList != null) {
                for (PmsProductSaleAttr pmsProductSaleAttr : spuSaleAttrList) {
                    //保存商品销售属性
                    pmsProductSaleAttr.setProductId(proId);
                    pmsProductSaleAttrMapper.insert(pmsProductSaleAttr);

                    List<PmsProductSaleAttrValue> spuSaleAttrValueList = pmsProductSaleAttr.getSpuSaleAttrValueList();
                    if (spuSaleAttrList != null) {

                        for (PmsProductSaleAttrValue pmsProductSaleAttrValue : spuSaleAttrValueList) {
                            //保存商品销售属性值
                            pmsProductSaleAttrValue.setProductId(proId);
                            pmsProductSaleAttrValueMapper.insert(pmsProductSaleAttrValue);
                        }
                    }
                }
            }
            //保存商品销售图片
            List<PmsProductImage> spuImageList = pmsProductInfo.getSpuImageList();
            if (spuImageList != null) {
                for (PmsProductImage pmsProductImage : spuImageList) {
                    pmsProductImage.setProductId(proId);
                    pmsProductImageMapper.insert(pmsProductImage);
                }
            }
        }
    }

    @Override
    public List<PmsProductImage> spuImageList(String spuId) {
        PmsProductImage pmsProductImage = new PmsProductImage();
        pmsProductImage.setProductId(spuId);
        return pmsProductImageMapper.select(pmsProductImage);
    }
}
