package com.myg.yxy.config;

import com.myg.yxy.interceptor.LoginInterceptor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class PlatformInterceptorConfiguration implements WebMvcConfigurer {

    @Bean
    public LoginInterceptor loginInterceptor(){
        return new LoginInterceptor();
    }

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(loginInterceptor())
            .addPathPatterns("/**")
            .excludePathPatterns("/**/error","/**/login", "/**/logout","/api/commodity/uploadData", "/**/*.html", "/**/*.js", "/**/*.css", "/**/*.woff", "/**/*.ttf", "/**/*.less", "/**/*.png", "/**/*.jpg", "/**/*.jpeg", "/**/*.gif", "/**/*.ico");
    }
}
