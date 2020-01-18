package com.atguigu.gmall.manage.service.impl;

import com.alibaba.dubbo.config.annotation.Service;
import com.atguigu.gmall.bean.*;
import com.atguigu.gmall.manage.mapper.*;
import com.atguigu.gmall.service.PmsBaseAttrService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;
import java.util.Set;

@Service
public class PmsBaseAttrServiceImpl implements PmsBaseAttrService {

    @Autowired
    PmsBaseAttrMapper pmsBaseAttrMapper;

    @Autowired
    PmsBaseAttrValueMapper pmsBaseAttrValueMapper;

    @Autowired
    PmsBaseSaleAttrMapper pmsBaseSaleAttrMapper;

    @Autowired
    PmsProductSaleAttrMapper pmsProductSaleAttrMapper;
    @Autowired
    PmsProductSaleAttrValueMapper pmsProductSaleAttrValueMapper;

    @Override
    public List<PmsBaseAttrInfo> getAttrInfoList(String catalog3Id) {
        PmsBaseAttrInfo pmsBaseAttrInfo = new PmsBaseAttrInfo();
        pmsBaseAttrInfo.setCatalog3Id(catalog3Id);
        List<PmsBaseAttrInfo> attrInfos = pmsBaseAttrMapper.select(pmsBaseAttrInfo);
        for (PmsBaseAttrInfo attrInfo : attrInfos) {
            PmsBaseAttrValue pmsBaseAttrValue = new PmsBaseAttrValue();
            pmsBaseAttrValue.setAttrId(attrInfo.getId());
            List<PmsBaseAttrValue> attrValues = pmsBaseAttrValueMapper.select(pmsBaseAttrValue);
            attrInfo.setAttrValueList(attrValues);
        }
        return attrInfos;
    }

    @Override
    public void saveAttrInfo(PmsBaseAttrInfo pmsBaseAttrInfo) {
        int i = pmsBaseAttrMapper.insert(pmsBaseAttrInfo);

        List<PmsBaseAttrValue> attrValueList = pmsBaseAttrInfo.getAttrValueList();
        for (PmsBaseAttrValue pmsBaseAttrValue : attrValueList) {
            pmsBaseAttrValue.setAttrId(pmsBaseAttrInfo.getId());
            pmsBaseAttrValueMapper.insert(pmsBaseAttrValue);
        }
    }

    @Override
    public List<PmsBaseSaleAttr> getBaseSaleAttrList() {
        return pmsBaseSaleAttrMapper.selectAll();
    }

    @Override
    public List<PmsProductSaleAttr> spuSaleAttrList(String spuId) {
        PmsProductSaleAttr pmsProductSaleAttr = new PmsProductSaleAttr();
        pmsProductSaleAttr.setProductId(spuId);
        List<PmsProductSaleAttr> saleAttrs = pmsProductSaleAttrMapper.select(pmsProductSaleAttr);
        for (PmsProductSaleAttr saleAttr : saleAttrs) {
            PmsProductSaleAttrValue saleAttrValue = new PmsProductSaleAttrValue();
            saleAttrValue.setProductId(spuId);
            saleAttrValue.setSaleAttrId(saleAttr.getSaleAttrId());
            List<PmsProductSaleAttrValue> attrValues = pmsProductSaleAttrValueMapper.select(saleAttrValue);
            saleAttr.setSpuSaleAttrValueList(attrValues);
        }
        return saleAttrs;
    }

    @Override
    public List<PmsProductSaleAttr> spuSaleAttrListCheckBySku(String spuId, String id) {
        List<PmsProductSaleAttr> PmsProductSaleAttrs =  pmsProductSaleAttrMapper.selectSpuSaleAttrListCheckBySku(spuId,id);
        return PmsProductSaleAttrs;
    }

    @Override
    public List<PmsBaseAttrInfo> listByValuId(Set<String> set) {

        String valueIdStr = StringUtils.join(set, ",");

        List<PmsBaseAttrInfo> pmsBaseAttrInfos = pmsBaseAttrMapper.selectAttrList(valueIdStr);
        return pmsBaseAttrInfos;
    }


}
