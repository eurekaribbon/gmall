package com.atguigu.gmall.search;


import com.alibaba.dubbo.config.annotation.Reference;
import com.atguigu.gmall.bean.PmsSerachSkuInfo;
import com.atguigu.gmall.bean.PmsSkuInfo;
import com.atguigu.gmall.service.SkuService;
import io.searchbox.client.JestClient;
import io.searchbox.core.Index;
import io.searchbox.core.Search;
import io.searchbox.core.SearchResult;
import org.apache.commons.beanutils.BeanUtils;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.MatchQueryBuilder;
import org.elasticsearch.index.query.TermQueryBuilder;
import org.elasticsearch.search.builder.SearchSourceBuilder;
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
    public void search() throws IOException {

        SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder();
            //bool
        BoolQueryBuilder boolQueryBuilder = new BoolQueryBuilder();
                        //term
        TermQueryBuilder termQueryBuilder = new TermQueryBuilder("skuAttrValueList.attrId", "25");
                //filter
        boolQueryBuilder.filter(termQueryBuilder);
                        //match
        MatchQueryBuilder matchQueryBuilder = new MatchQueryBuilder("skuName", "华为");
                //must
        boolQueryBuilder.must(matchQueryBuilder);

        //query
        searchSourceBuilder.query(boolQueryBuilder);

        //from
        searchSourceBuilder.from(0);
        //size
        searchSourceBuilder.size(20);
        //highlight
        searchSourceBuilder.highlight(null);

        String s = searchSourceBuilder.toString();
        System.out.println(s);

        String json = "{\n" +
                "  \"query\": {\n" +
                "    \"bool\": {\n" +
                "      \"filter\": [\n" +
                "        {\n" +
                "          \"term\": {\n" +
                "                    \"skuAttrValueList.attrId\": \"25\"\n" +
                "                  }\n" +
                "        },\n" +
                "        {\n" +
                "          \"term\": {\n" +
                "              \"skuAttrValueList.valueId\": \"39\"\n" +
                "                  }\n" +
                "          \n" +
                "        }\n" +
                "      ],\n" +
                "      \"must\": [\n" +
                "        {\n" +
                "          \"match\": {\n" +
                "            \"skuName\": \"华为\"\n" +
                "          }\n" +
                "        }\n" +
                "      ]\n" +
                "    }\n" +
                "  }\n" +
                "}";
        Search search = new Search.Builder(searchSourceBuilder.toString()).addIndex("gmallpms").addType("PmsSkuInfo").build();
        SearchResult result = jestClient.execute(search);
        List<SearchResult.Hit<PmsSerachSkuInfo, Void>> hits = result.getHits(PmsSerachSkuInfo.class);
        for (SearchResult.Hit<PmsSerachSkuInfo, Void> hit : hits) {
            PmsSerachSkuInfo pmsSerachSkuInfo = hit.source;

        }
    }


//同步mysql库与elasticsearch库
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
