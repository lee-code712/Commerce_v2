package com.digital.v2.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import com.digital.v2.interceptor.AuthInterceptor;

@Configuration
public class WebMvcConfig implements WebMvcConfigurer {

	@Override
    public void addInterceptors(InterceptorRegistry registry) {
		
		// 인증 관련 인터셉터 설정
        registry.addInterceptor(new AuthInterceptor())
                .addPathPatterns(AuthInterceptor.authEssential)
                .excludePathPatterns(AuthInterceptor.authInessential);
    }
	
}
