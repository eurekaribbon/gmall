package com.atguigu.gmall.passport.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
public class PassPortController {


    @RequestMapping("login")
    public String login(){
        return "index";
    }
}
