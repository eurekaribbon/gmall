package com.atguigu.gmall.webMvcConfig;

import com.atguigu.gmall.inteceptors.AuthInteceptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;

@Configuration
public class WebMvcConfig extends WebMvcConfigurerAdapter {

    @Autowired
    AuthInteceptor authInteceptor;

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(authInteceptor).addPathPatterns("/*");
        super.addInterceptors(registry);
    }
}
