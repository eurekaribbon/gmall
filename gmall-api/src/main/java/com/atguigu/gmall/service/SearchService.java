package com.atguigu.gmall.service;

import com.atguigu.gmall.bean.PmsSearchParam;
import com.atguigu.gmall.bean.PmsSerachSkuInfo;

import java.util.List;

public interface SearchService {
    /**
     * 搜索列表查询
     * @param pmsSearchParam
     * @return
     */
    List<PmsSerachSkuInfo> list(PmsSearchParam pmsSearchParam);
}
