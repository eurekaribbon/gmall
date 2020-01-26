package com.atguigu.gmall;

import com.atguigu.gmall.util.JwtUtil;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * @author lvlei
 * create on 2020-01-26-9:04
 */
public class TestJwt {
    public static void main(String[] args) {

        String salt = "127.0.0.1"+new SimpleDateFormat("hh:mm:ss").format(new Date());
        Map<String,Object> map = new HashMap<>();
        map.put("uId","123");
        String s = JwtUtil.encode("gmall", map, salt);
        System.out.println(s);

        Map<String, Object> gmall = JwtUtil.decode(s, "gmall", salt);
        System.out.println(gmall);
    }
}
