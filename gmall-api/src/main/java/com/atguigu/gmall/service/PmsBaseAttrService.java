package com.atguigu.gmall.service;

import com.atguigu.gmall.bean.PmsBaseAttrInfo;

import java.util.List;

public interface PmsBaseAttrService {
    List<PmsBaseAttrInfo> getAttrInfoList(String catalog3Id);

    void saveAttrInfo(PmsBaseAttrInfo pmsBaseAttrInfo);
}
