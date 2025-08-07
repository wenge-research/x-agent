package com.wg.appframe.core.interceptor;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.util.Arrays;

/**
 * @Description: 拦截器配置
 * @Author: CHENZHIWEI
 * @CreateTime: 2022-04-27 14:04:12
 */
@Configuration
public class AuthInterceptorConfig implements WebMvcConfigurer {

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        //addPathPatterns 添加拦截规则
        registry.addInterceptor(getInterceptor()).addPathPatterns("/**")
                .excludePathPatterns("/authentication/**")
                .excludePathPatterns("/stat/warReport/**")
                .excludePathPatterns("/swagger-resources/**")
                .excludePathPatterns("/webjars/**")
                .excludePathPatterns("/v2/**")
                .excludePathPatterns("/swagger-ui.html/**")
                .addPathPatterns("/login")
                .excludePathPatterns(Arrays.asList("/swagger**/**","/doc.html**/**","/swagger-resources/**","/webjars/**","/v3/**","/error","/favicon.ico"));
    }

    @Bean
    AuthInterceptor getInterceptor() {
        return new AuthInterceptor();
    }
}
