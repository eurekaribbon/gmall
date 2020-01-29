package com.atguigu.gmall.passport.controller;

import com.alibaba.dubbo.config.annotation.Reference;
import com.alibaba.fastjson.JSON;
import com.atguigu.gmall.bean.UmsMember;
import com.atguigu.gmall.service.UmemService;
import com.atguigu.gmall.util.HttpclientUtil;
import com.atguigu.gmall.util.JwtUtil;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.Map;

@Controller
public class PassPortController {

    @Reference
    UmemService umemService;


    @RequestMapping("index")
    public String index(String returnUrl, ModelMap map){
        map.put("returnUrl",returnUrl);
        return "index";
    }


    @RequestMapping("verify")
    @ResponseBody
    public String verify(String token,String currentIp){
        Map<String,String> map = new HashMap<>();

        Map<String, Object> tokenJson = JwtUtil.decode(token, "gmall", currentIp);
        if(tokenJson!=null){
            map.put("status","success");
            map.put("memberId", (String) tokenJson.get("memberId"));
            map.put("nickname", (String) tokenJson.get("nickname"));
        }else{
            map.put("status","fail");
        }
        return JSON.toJSONString(map);
    }


    @RequestMapping("vlogin")
    public String vlogin(String code,HttpServletRequest request){
        //根据code获取accesstoken
        String s3 = "https://api.weibo.com/oauth2/access_token?";
        Map<String,String> paramMap = new HashMap<>();
        paramMap.put("client_id","5492332");
        paramMap.put("client_secret","af7dfaa75c67d99277f3a20251af5924");
        paramMap.put("grant_type","authorization_code");
        paramMap.put("redirect_uri","http://passport.gmall.com:8085/vlogin");
        paramMap.put("code",code);
        String access_token_json = HttpclientUtil.doPost(s3, paramMap);
        Map<String,String> access_map = JSON.parseObject(access_token_json, Map.class);

        String accessToken = access_map.get("access_token");
        String uId = access_map.get("uid");
        //根据token获取用户信息
        String url4 = "https://api.weibo.com/2/users/show.json?access_token="+accessToken+"&uid="+uId;
        String umemberJson = HttpclientUtil.doGet(url4);
        Map<String,String> map = JSON.parseObject(umemberJson, Map.class);
        //保存用户信息
        UmsMember umsMember = new UmsMember();
        umsMember.setSourceType("2");
        umsMember.setSourceUid(uId);
        umsMember.setAccessToken(accessToken);
        umsMember.setCode(code);
        umsMember.setNickname(map.get("name"));

        UmsMember umsMember1 = new UmsMember();
        umsMember1.setSourceUid(uId);
        UmsMember umsMemberCheck = umemService.umsMembercheck(umsMember1);
        if(umsMemberCheck==null){
            umsMemberCheck = umemService.addOauthUser(umsMember);
        }
        //token生成
        Map<String,Object> map1 = new HashMap<>();
        String memberId = umsMemberCheck.getId();//id未拿到
        String nickname = umsMemberCheck.getNickname();
        map1.put("memberId",memberId);
        map1.put("nickname",nickname);

        String ip = "";
        String token = "";
        ip = request.getHeader("x_forwarded_for");
        if(StringUtils.isBlank(ip)){
            ip = request.getRemoteAddr();
        }

        token = JwtUtil.encode("gmall", map1, ip);

        //缓存存储
        umemService.addToken(token,memberId);

        return "redirect:http://search.gmall.com:8083/index?token="+token;
    }

    @RequestMapping("login")
    @ResponseBody
    public String login(UmsMember umsMember, HttpServletRequest request){
        UmsMember umsMemberLogin = umemService.login(umsMember);
        String token = "";
        if(umsMemberLogin!=null){
           //生成token
            Map<String,Object> map = new HashMap<>();
            String memberId = umsMemberLogin.getId();
            String nickname = umsMemberLogin.getNickname();
            map.put("memberId",memberId);
            map.put("nickname",nickname);


            String ip = "";
            ip = request.getHeader("x_forwarded_for");
            if(StringUtils.isBlank(ip)){
                ip = request.getRemoteAddr();
            }

            token = JwtUtil.encode("gmall", map, ip);

            //缓存存储
            umemService.addToken(token,memberId);
            return token;
        }
        //登录失败
        return "fail";
    }

}
