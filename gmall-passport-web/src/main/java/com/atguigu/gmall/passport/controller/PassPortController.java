package com.atguigu.gmall.passport.controller;

import com.atguigu.gmall.bean.UmsMember;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
public class PassPortController {


    @RequestMapping("index")
    public String index(String returnUrl, ModelMap map){
        map.put("returnUrl",returnUrl);
        return "index";
    }


    @RequestMapping("verify")
    @ResponseBody
    public String verify(String token){

        return "success";
    }


    @RequestMapping("login")
    @ResponseBody
    public String login(UmsMember umsMember){
        String token = "";

        return "token";
    }

}
