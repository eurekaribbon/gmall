package com.atguigu.gmall.manage.service.impl;

import com.alibaba.dubbo.config.annotation.Service;
import com.atguigu.gmall.bean.PmsBaseCatalog1;
import com.atguigu.gmall.manage.mapper.CatalogMapper;
import com.atguigu.gmall.service.CatalogService;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

/**
 * @author lvlei
 * create on 2019-12-24-22:05
 */
@Service
public class CatalogServiceImpl  implements CatalogService {

    @Autowired
    CatalogMapper catalogMapper;

    @Override
    public List<PmsBaseCatalog1> getCatalog1() {
        return catalogMapper.selectAll();
    }
}
