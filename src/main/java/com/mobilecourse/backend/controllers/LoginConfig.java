package com.mobilecourse.backend.controllers;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class LoginConfig implements WebMvcConfigurer {
	@Override
	public void addInterceptors(InterceptorRegistry registry) {
		registry.addInterceptor(new LoginInterceptor())
				.addPathPatterns("/**")
				.excludePathPatterns("/user/login")
				.excludePathPatterns("/user/activate")
				.excludePathPatterns("/user/register")
				.excludePathPatterns("/websocket/**");
				// orderImage在调试时考虑临时开放
				// .excludePathPatterns("/orderImage/**");
	}
}
