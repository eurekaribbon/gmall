package com.atguigu.gmall.passport.controller;

import com.alibaba.fastjson.JSON;
import com.atguigu.gmall.util.HttpclientUtil;

import java.util.HashMap;
import java.util.Map;

/**
 * @author lvlei
 * create on 2020-01-28-16:52
 */
public class TestOauth2 {

    public static void main(String[] args) {
        String url1 = "https://api.weibo.com/oauth2/authorize?client_id=5492332&response_type=code&redirect_uri=http://passport.gmall.com:8085/vlogin";
        //String s = HttpclientUtil.doGet(url1);
        //System.out.println(s);

        String url2 = "http://passport.gmall.com:8085/vlogin?code=63b953bac807c97ea585d2746f1f3609";

        //

        String s3 = "https://api.weibo.com/oauth2/access_token?";//?client_id=187638711&client_secret=a79777bba04ac70d973ee002d27ed58c&grant_type=authorization_code&redirect_uri=http://passport.gmall.com:8085/vlogin&code=CODE";
        Map<String,String> paramMap = new HashMap<>();
        paramMap.put("client_id","5492332");
        paramMap.put("client_secret","af7dfaa75c67d99277f3a20251af5924");
        paramMap.put("grant_type","authorization_code");
        paramMap.put("redirect_uri","http://passport.gmall.com:8085/vlogin");
        paramMap.put("code","a35f88660a1e71551e15cfc4af59153f");
        /*String doPost = HttpclientUtil.doPost(s3, paramMap);
        System.out.println(doPost);*/

        String url4 = "https://api.weibo.com/2/users/show.json?access_token=2.00j2TXsF00AoC462795eb2c80GtTua&uid=1";
        String umemberJson = HttpclientUtil.doGet(url4);
        Map<String,String> map = JSON.parseObject(umemberJson, Map.class);
        System.out.println(map);
    }
}
