package com.mobilecourse.backend.controllers;

import org.springframework.context.annotation.Configuration;
import org.springframework.lang.Nullable;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

public class LoginInterceptor implements HandlerInterceptor {
	@Override
	public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {

		System.out.println("==========登录状态拦截");

		HttpSession session = request.getSession();
		System.out.println("sessionId为：" + session.getId());

		// 获取用户信息，如果没有用户信息直接返回提示信息
		// Object userInfo = session.getAttribute("userInfo");
		Object uid = session.getAttribute("uid");
		if (uid == null) {
			System.out.println("没有登录");
			response.getWriter().write("Please Login In.");
			return false;
		} else {
			System.out.println("已经登录过啦，uid为：" + session.getAttribute("uid"));
		}

		return true;
	}

	@Override
	public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler, @Nullable ModelAndView modelAndView) throws Exception {

	}

	@Override
	public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, @Nullable Exception ex) throws Exception {

	}
}

