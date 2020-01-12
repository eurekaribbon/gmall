package com.atguigu.gmall.service;

import com.atguigu.gmall.bean.PmsSkuInfo;

import java.util.List;

public interface SkuService {

    /**
     * 保存skuinfo信息
     * @param pmsSkuInfo
     */
    void saveSkuInfo(PmsSkuInfo pmsSkuInfo);

    /**
     * 通过skuId获取sku信息
     * @param skuId
     * @return
     */
    PmsSkuInfo getSkuInfoBySkuId(String skuId);

    /**
     * 通过spuId获取所有的sku信息
     * @param spuId
     * @return
     */
    List<PmsSkuInfo> getSkuInfoBySpuId(String spuId);
}
