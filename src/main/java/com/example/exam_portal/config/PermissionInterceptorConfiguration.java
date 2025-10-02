package com.example.exam_portal.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class PermissionInterceptorConfiguration implements WebMvcConfigurer{
    @Bean
    PermissionInterceptor getPermissionInterceptor() {
        return new PermissionInterceptor();
    }

    @Override
    public void addInterceptors(InterceptorRegistry registry) {// https://stackoverflow.com/a/34974725
        String[] whiteList = {"/login/**", "/register",
                "/", "/api/v1/auth/**", "/api/v1/user/**", "/admin/**", "/css/**",
                "/js/**", "/img/**", "/fonts/**", "/uploads/**",
                "/api/v1/listresult/**", "/api/chat/**"
        };
        registry.addInterceptor(getPermissionInterceptor())
                .excludePathPatterns(whiteList);
    }
}
