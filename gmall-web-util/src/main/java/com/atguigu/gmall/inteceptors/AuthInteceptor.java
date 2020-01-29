package com.atguigu.gmall.inteceptors;

import com.alibaba.fastjson.JSON;
import com.atguigu.gmall.anotations.LoginRequire;
import com.atguigu.gmall.util.CookieUtil;
import com.atguigu.gmall.util.HttpclientUtil;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Map;

@Component
public class AuthInteceptor implements HandlerInterceptor {
    @Override
    public boolean preHandle(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, Object o) throws Exception {
        HandlerMethod method = (HandlerMethod) o;
        LoginRequire loginRequire = method.getMethodAnnotation(LoginRequire.class);
        if(loginRequire==null){
            //不登录可以访问
            return true;
        }
        System.out.println("进入拦截器拦截功能");

        //获取token 验证真伪
        String token = "";
        String oldToken = CookieUtil.getCookieValue(httpServletRequest, "oldToken", true);
        if(StringUtils.isNotBlank(oldToken)){
            token = oldToken;
        }
        String newToken = httpServletRequest.getParameter("token");
        if(StringUtils.isNotBlank(newToken)){
            token = newToken;
        }
        String success = "fail";
        Map<String,String> map = null;
        if(StringUtils.isNotBlank(token)){
            String ip = "";
            ip = httpServletRequest.getHeader("x_forwarded_for");
            if(StringUtils.isBlank(ip)){
                ip = httpServletRequest.getRemoteAddr();
            }
            String successJson = HttpclientUtil.doGet("http://localhost:8085/verify?token="+token+"&currentIp="+ip);
            map = JSON.parseObject(successJson, Map.class);
            success = map.get("status");
        }

        boolean loginSuccess = loginRequire.loginSuccess();
        if(loginSuccess){
            //必须登录才可以访问
            if(!success.equals("success")){//验证token失败
                //重定向到登陆页
                StringBuffer requestURL = httpServletRequest.getRequestURL();
                httpServletResponse.sendRedirect("http://localhost:8085/index?returnUrl="+requestURL);
                return false;
            }
            //覆盖cookie中的token
            httpServletRequest.setAttribute("memberId",map.get("memberId"));
            httpServletRequest.setAttribute("nickname",map.get("nickname"));
            //覆盖cookie中的值
            CookieUtil.setCookie(httpServletRequest,httpServletResponse,"oldToken",token,60*60*2,true);

        }else{
            //不是必须登录
            if(success.equals("success")){
                httpServletRequest.setAttribute("memberId",map.get("memberId"));
                httpServletRequest.setAttribute("nickname",map.get("nickname"));
                //覆盖cookie中的值
                CookieUtil.setCookie(httpServletRequest,httpServletResponse,"oldToken",token,60*60*2,true);
            }
        }

        return true;
    }

    @Override
    public void postHandle(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, Object o, ModelAndView modelAndView) throws Exception {

    }

    @Override
    public void afterCompletion(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, Object o, Exception e) throws Exception {

    }
}
