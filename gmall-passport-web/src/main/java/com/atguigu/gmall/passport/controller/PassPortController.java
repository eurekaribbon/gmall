package com.atguigu.gmall.passport.controller;

import com.atguigu.gmall.anotations.LoginRequire;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
public class PassPortController {


    @RequestMapping("index")
    public String login(){
        return "index";
    }
}
