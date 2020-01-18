package com.atguigu.gmall.search.controller;

import com.alibaba.dubbo.config.annotation.Reference;
import com.atguigu.gmall.bean.*;
import com.atguigu.gmall.service.PmsBaseAttrService;
import com.atguigu.gmall.service.SearchService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

/**
 * @author lvlei
 * create on 2019-12-29-15:10
 */
@Controller
public class SearchController {

    @Reference
    SearchService searchService;

    @Reference
    PmsBaseAttrService pmsBaseAttrService;

    @RequestMapping("index")
    public String index(){
        return "index";
    }

    @RequestMapping("list.html")
    public String list(PmsSearchParam pmsSearchParam, ModelMap modelMap){

        //调用搜索服务返回搜索结果
        List<PmsSerachSkuInfo> pmsSerachSkuInfos = searchService.list(pmsSearchParam);

        //抽取所有的属性值id
        if (pmsSerachSkuInfos.size()>0) {
            Set<String> set = new HashSet<>();
            for (PmsSerachSkuInfo pmsSerachSkuInfo : pmsSerachSkuInfos) {
                List<PmsSkuAttrValue> skuAttrValueList = pmsSerachSkuInfo.getSkuAttrValueList();
                for (PmsSkuAttrValue pmsSkuAttrValue : skuAttrValueList) {
                    set.add(pmsSkuAttrValue.getValueId());
                }
            }

            //根据属性值id 查属性值列表
            List<PmsBaseAttrInfo> pmsBaseAttrInfos = pmsBaseAttrService.listByValuId(set);

            //需要对平台属性值进一步处理  去掉当前条件中valueId所在的属性组
            String[] delValueIds = pmsSearchParam.getValueId();
            Iterator<PmsBaseAttrInfo> iterator = pmsBaseAttrInfos.iterator();
            if(delValueIds!=null) {
                while (iterator.hasNext()) {
                    PmsBaseAttrInfo pmsBaseAttrInfo = iterator.next();
                    List<PmsBaseAttrValue> attrValueList = pmsBaseAttrInfo.getAttrValueList();
                    for (PmsBaseAttrValue pmsBaseAttrValue : attrValueList) {
                        String vId = pmsBaseAttrValue.getId();
                        for (String delValueId : delValueIds) {
                            if (delValueId.equals(vId)) {
                                iterator.remove();
                            }
                        }
                    }
                }
            }
            modelMap.put("attrList",pmsBaseAttrInfos);

            //面包屑
            String keyword = pmsSearchParam.getKeyword();
            if(StringUtils.isNotBlank(keyword)){
                modelMap.put("keyword",keyword);
            }
            List<PmsSearchCrumb> pmsSearchCrumbs = new java.util.ArrayList();
            if(delValueIds!=null){
                for (String delValueId : delValueIds) {
                    PmsSearchCrumb pmsSearchCrumb = new PmsSearchCrumb();
                    pmsSearchCrumb.setValueId(delValueId);
                    pmsSearchCrumb.setValueName(delValueId);
                    String urlParam = getUrlParam(pmsSearchParam,delValueId);
                    pmsSearchCrumb.setUrlParam(urlParam);
                    pmsSearchCrumbs.add(pmsSearchCrumb);
                }
            }
            modelMap.put("attrValueSelectedList",pmsSearchCrumbs);
        }
        String urlParam = getUrlParam(pmsSearchParam);
        modelMap.put("urlParam",urlParam);

        modelMap.put("skuLsInfoList",pmsSerachSkuInfos);
        return "list";
    }

    private String getUrlParam(PmsSearchParam pmsSearchParam,String ...delvalueId) {
        String keyword = pmsSearchParam.getKeyword();
        String catalog3Id = pmsSearchParam.getCatalog3Id();
        String[] skuAttrValueList = pmsSearchParam.getValueId();

        String urlParam = "";

        if(StringUtils.isNotBlank(keyword)){
            if(StringUtils.isNotBlank(urlParam)){
                urlParam = urlParam + "&";
            }
            urlParam = urlParam+"keyword="+keyword;
        }
        if(StringUtils.isNotBlank(catalog3Id)){
            if(StringUtils.isNotBlank(urlParam)){
                urlParam = urlParam + "&";
            }
            urlParam = urlParam+"catalog3Id="+catalog3Id;
        }
        if(skuAttrValueList!=null){
            for (String  pmsSkuAttrValue : skuAttrValueList) {
                if(delvalueId.length>0){
                    if(!pmsSkuAttrValue.equals(delvalueId[0])){
                        urlParam = urlParam +"&valueId="+pmsSkuAttrValue;
                    }
                }else{
                    urlParam = urlParam +"&valueId="+pmsSkuAttrValue;
                }

            }
        }
        return urlParam;
    }

}
