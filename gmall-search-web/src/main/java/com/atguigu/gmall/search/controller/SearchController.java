package com.atguigu.gmall.search.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * @author lvlei
 * create on 2019-12-29-15:10
 */
@Controller
public class SearchController {

    @RequestMapping("index")
    public String index(){
        return "index";
    }

    @RequestMapping("list.html")
    public String list(){
        return "list";
    }

}
