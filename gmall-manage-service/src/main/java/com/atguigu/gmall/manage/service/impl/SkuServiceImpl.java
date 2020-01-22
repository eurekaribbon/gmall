package com.atguigu.gmall.manage.service.impl;

import com.alibaba.dubbo.config.annotation.Service;
import com.alibaba.fastjson.JSON;
import com.atguigu.gmall.bean.PmsSkuAttrValue;
import com.atguigu.gmall.bean.PmsSkuImage;
import com.atguigu.gmall.bean.PmsSkuInfo;
import com.atguigu.gmall.bean.PmsSkuSaleAttrValue;
import com.atguigu.gmall.manage.mapper.PmsSkuAttrValueMapper;
import com.atguigu.gmall.manage.mapper.PmsSkuImageMapper;
import com.atguigu.gmall.manage.mapper.PmsSkuInfoMapper;
import com.atguigu.gmall.manage.mapper.PmsSkuSaleAttrValueMapper;
import com.atguigu.gmall.service.SkuService;
import com.atguigu.gmall.util.RedisUtil;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import redis.clients.jedis.Jedis;

import java.util.List;
import java.util.UUID;

/**
 * @author lvlei
 * create on 2020-01-01-22:06
 */

@Service
public class SkuServiceImpl implements SkuService {
    @Autowired
    PmsSkuInfoMapper pmsSkuInfoMapper;

    @Autowired
    PmsSkuImageMapper pmsSkuImageMapper;

    @Autowired
    PmsSkuAttrValueMapper pmsSkuAttrValueMapper;

    @Autowired
    PmsSkuSaleAttrValueMapper pmsSkuSaleAttrValueMapper;

    @Autowired
    RedisUtil redisUtil;


    @Override
    public void saveSkuInfo(PmsSkuInfo pmsSkuInfo) {
        int i = pmsSkuInfoMapper.insert(pmsSkuInfo);
        if(i>0){
            String skuInfoId = pmsSkuInfo.getId();
            List<PmsSkuImage> skuImageList = pmsSkuInfo.getSkuImageList();
            for (PmsSkuImage skuImage : skuImageList) {
                //保存图片
                skuImage.setSkuId(skuInfoId);
                pmsSkuImageMapper.insert(skuImage);
            }

            List<PmsSkuAttrValue> skuAttrValueList = pmsSkuInfo.getSkuAttrValueList();
            for (PmsSkuAttrValue pmsSkuAttrValue : skuAttrValueList) {
                //保存平台属性
                pmsSkuAttrValue.setSkuId(skuInfoId);
                pmsSkuAttrValueMapper.insert(pmsSkuAttrValue);
            }

            List<PmsSkuSaleAttrValue> skuSaleAttrValueList = pmsSkuInfo.getSkuSaleAttrValueList();
            for (PmsSkuSaleAttrValue pmsSkuSaleAttrValue : skuSaleAttrValueList) {
                //保存商品sku销售属性
                pmsSkuSaleAttrValue.setSkuId(skuInfoId);
                pmsSkuSaleAttrValueMapper.insert(pmsSkuSaleAttrValue);
            }
        }

    }


    public PmsSkuInfo getSkuInfoFromDb(String skuId) {
        PmsSkuInfo pmsSkuInfo = new PmsSkuInfo();
        pmsSkuInfo.setId(skuId);
        PmsSkuInfo skuInfo = pmsSkuInfoMapper.selectOne(pmsSkuInfo);
        PmsSkuImage pmsSkuImage = new PmsSkuImage();
        pmsSkuImage.setSkuId(skuId);
        List<PmsSkuImage> skuImageList = pmsSkuImageMapper.select(pmsSkuImage);
        skuInfo.setSkuImageList(skuImageList);
        return skuInfo;
    }


    @Override
    public PmsSkuInfo getSkuInfoBySkuId(String skuId) {
        Jedis jedis = redisUtil.getJedis();
        //查询缓存 key(数据对象名:id:对象属性) sku:108:info
        String key = "sku:"+skuId+":info";//前缀  后缀写在常量类中
        String skuInfo = jedis.get(key);
        PmsSkuInfo pmsSkuInfo = new PmsSkuInfo();
        if(StringUtils.isNotBlank(skuInfo)){
            pmsSkuInfo = JSON.parseObject(skuInfo, PmsSkuInfo.class);
        }else{
        //查询数据库
            //防止缓存击穿  采用分布式锁
            String lockKey ="sku:"+skuId+":lock";
            String value = UUID.randomUUID().toString();
            String ok = jedis.set(lockKey, value, "nx", "px", 1000);
            if(StringUtils.isNotBlank(ok)&&"OK".equals(ok)){
                //设置成功
                pmsSkuInfo = getSkuInfoFromDb(skuId);
                if(pmsSkuInfo!=null){
                    //查询结果存入缓存
                    jedis.setex(key, 60*10,JSON.toJSONString(pmsSkuInfo));
                }else{
                    //防止缓存穿透  设置null  空串
                    jedis.setex(key, 60*3,JSON.toJSONString(""));
                }
                String lock = jedis.get(lockKey);
                if(StringUtils.isNotBlank(lock)&&value.equals(lock)){
                    //jedis.eval("", Collections.EMPTY_LIST,Collections.EMPTY_LIST); //利用lua脚本删除锁   防止删除别人的锁
                    jedis.del(lockKey);
                }
                jedis.close();
            }else{
                //设置失败 自旋
                try {
                    Thread.sleep(3000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                return getSkuInfoBySkuId(skuId);
            }
        }

        return pmsSkuInfo;
    }


    @Override
    public List<PmsSkuInfo> getSkuInfoBySpuId(String spuId) {
        PmsSkuInfo pmsSkuInfo = new PmsSkuInfo();
        pmsSkuInfo.setSpuId(spuId);
        List<PmsSkuInfo> skuInfos = pmsSkuInfoMapper.select(pmsSkuInfo);
        for (PmsSkuInfo skuInfo : skuInfos) {
            PmsSkuSaleAttrValue saleAttrValue = new PmsSkuSaleAttrValue();
            saleAttrValue.setSkuId(skuInfo.getId());
            List<PmsSkuSaleAttrValue> saleAttrValues = pmsSkuSaleAttrValueMapper.select(saleAttrValue);
            skuInfo.setSkuSaleAttrValueList(saleAttrValues);
        }
        return skuInfos;
    }

    @Override
    public List<PmsSkuInfo> getAllSku() {
        List<PmsSkuInfo> pmsSkuInfos = pmsSkuInfoMapper.selectAll();
        for (PmsSkuInfo pmsSkuInfo : pmsSkuInfos) {
            PmsSkuAttrValue pmsSkuAttrValue = new PmsSkuAttrValue();
            pmsSkuAttrValue.setSkuId(pmsSkuInfo.getId());
            List<PmsSkuAttrValue> attrValues = pmsSkuAttrValueMapper.select(pmsSkuAttrValue);
            pmsSkuInfo.setSkuAttrValueList(attrValues);
        }
        return pmsSkuInfos;
    }
}
