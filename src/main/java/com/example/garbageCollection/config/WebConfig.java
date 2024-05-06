package com.example.garbageCollection.config;

import com.example.garbageCollection.config.interceptor.JwtInterceptor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurationSupport;

/**
 * 注册拦截器
 */
@Configuration
public class WebConfig extends WebMvcConfigurationSupport {

    /**
     * 添加jwt拦截器
     *
     * @param registry
     */
    /*
    * 通过测试，发现拦截器失效， 通过new 的方式注入的JwtInterceptor() 不生效
    * **/
    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(jwtInterceptor())
                // 拦截所有请求，通过判断token 是否合法决定是否需要登录
                .addPathPatterns("/**")
                // 排除的，不需要token也可登录的东西
                .excludePathPatterns("/user/login","/user/register","/businessuser/login","/businessuser/register","/file/**");
    }

    /**
     * jwt拦截器，需要注入!! 通过bean 注入
     *
     * @return
     */
    @Bean
    public JwtInterceptor jwtInterceptor() {
        return new JwtInterceptor();
    }
}
