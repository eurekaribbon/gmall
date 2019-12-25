package com.atguigu.gmall.service;

import com.atguigu.gmall.bean.PmsProductInfo;

import java.util.List;

public interface PmsProductService {
    List<PmsProductInfo> getSpuList(String catalog3Id);
}
