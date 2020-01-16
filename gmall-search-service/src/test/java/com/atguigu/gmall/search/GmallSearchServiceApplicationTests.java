package com.atguigu.gmall.search;


import com.alibaba.dubbo.config.annotation.Reference;
import com.atguigu.gmall.bean.PmsSerachSkuInfo;
import com.atguigu.gmall.bean.PmsSkuInfo;
import com.atguigu.gmall.service.SkuService;
import io.searchbox.client.JestClient;
import io.searchbox.core.Index;
import org.apache.commons.beanutils.BeanUtils;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.util.List;

@SpringBootTest
@RunWith(SpringRunner.class)
public class GmallSearchServiceApplicationTests {

    @Reference
    SkuService skuService;

    @Autowired
    JestClient jestClient;



    @Test
    public void contextLoads() throws InvocationTargetException, IllegalAccessException, IOException {
        //查询mysql
        List<PmsSerachSkuInfo> lists = new java.util.ArrayList<>();
        List<PmsSkuInfo> skuInfoList = skuService.getAllSku();

        //转化为es数据结构
        for (PmsSkuInfo pmsSkuInfo : skuInfoList) {
            PmsSerachSkuInfo serachSkuInfo = new PmsSerachSkuInfo();
            BeanUtils.copyProperties(serachSkuInfo,pmsSkuInfo);
            lists.add(serachSkuInfo);
        }

        //导入es
        for (PmsSerachSkuInfo list : lists) {
            Index index = new Index.Builder(list).index("gmallpms").type("PmsSkuInfo").id(list.getId()).build();
            jestClient.execute(index);
        }

    }

}
