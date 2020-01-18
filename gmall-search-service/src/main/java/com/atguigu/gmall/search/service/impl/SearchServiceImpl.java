package com.atguigu.gmall.search.service.impl;

import com.alibaba.dubbo.config.annotation.Service;
import com.atguigu.gmall.bean.PmsSearchParam;
import com.atguigu.gmall.bean.PmsSerachSkuInfo;
import com.atguigu.gmall.service.SearchService;
import io.searchbox.client.JestClient;
import io.searchbox.core.Search;
import io.searchbox.core.SearchResult;
import org.apache.commons.lang3.StringUtils;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.MatchQueryBuilder;
import org.elasticsearch.index.query.TermQueryBuilder;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.elasticsearch.search.highlight.HighlightBuilder;
import org.elasticsearch.search.sort.SortOrder;
import org.springframework.beans.factory.annotation.Autowired;

import java.io.IOException;
import java.util.List;
import java.util.Map;

/**
 * @author lvlei
 * create on 2020-01-18-14:17
 */
@Service
public class SearchServiceImpl implements SearchService {

    @Autowired
    JestClient jestClient;

    @Override
    public List<PmsSerachSkuInfo> list(PmsSearchParam pmsSearchParam) {
        String dsl = getDsl(pmsSearchParam);
        Search search = new Search.Builder(dsl).addIndex("gmallpms").addType("PmsSkuInfo").build();
        SearchResult result = null;
        try {
            result = jestClient.execute(search);
        } catch (IOException e) {
            e.printStackTrace();
        }
        List<PmsSerachSkuInfo> pmsSerachSkuInfos =  new java.util.ArrayList<>();
        List<SearchResult.Hit<PmsSerachSkuInfo, Void>> hits = result.getHits(PmsSerachSkuInfo.class);
        for (SearchResult.Hit<PmsSerachSkuInfo, Void> hit : hits) {
            PmsSerachSkuInfo pmsSerachSkuInfo = hit.source;
            //高亮显示一个字段
            Map<String, List<String>> highlight = hit.highlight;
            if(highlight!=null&&highlight.size()>0){
                String skuName = highlight.get("skuName").get(0);
                pmsSerachSkuInfo.setSkuName(skuName);
            }
            pmsSerachSkuInfos.add(pmsSerachSkuInfo);
        }
        return pmsSerachSkuInfos;
    }

    private String getDsl(PmsSearchParam pmsSearchParam) {
        String[] skuAttrValueList = pmsSearchParam.getValueId();
        String keyword = pmsSearchParam.getKeyword();
        String catalog3Id = pmsSearchParam.getCatalog3Id();

        SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder();
        //bool
        BoolQueryBuilder boolQueryBuilder = new BoolQueryBuilder();

        if (skuAttrValueList != null) {
            for (String pmsSkuAttrValue : skuAttrValueList) {
                //term
                TermQueryBuilder termQueryBuilder = new TermQueryBuilder("skuAttrValueList.valueId", pmsSkuAttrValue);
                //filter
                boolQueryBuilder.filter(termQueryBuilder);
            }
        }
        if(StringUtils.isNotBlank(keyword)){
            //match
            MatchQueryBuilder matchQueryBuilder = new MatchQueryBuilder("skuName", keyword);
            //must
            boolQueryBuilder.must(matchQueryBuilder);
        }

        if(StringUtils.isNotBlank(catalog3Id)){
            //term
            TermQueryBuilder termQueryBuilder = new TermQueryBuilder("catalog3Id", catalog3Id);
            //filter
            boolQueryBuilder.filter(termQueryBuilder);
        }

        //query
        searchSourceBuilder.query(boolQueryBuilder);

        //from
        searchSourceBuilder.from(0);
        //size
        searchSourceBuilder.size(100);
        //highlight  高亮显示
        HighlightBuilder highlightBuilder = new HighlightBuilder();
        highlightBuilder.preTags("<span style='color:red'>");
        highlightBuilder.field("skuName");
        highlightBuilder.postTags("</span>");
        searchSourceBuilder.highlight(highlightBuilder);

        //sort
        searchSourceBuilder.sort("id", SortOrder.DESC);

        System.out.println(searchSourceBuilder.toString());
        return searchSourceBuilder.toString();
    }
}
