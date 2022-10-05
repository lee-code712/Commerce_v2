package com.digital.v2.interceptor;

import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import com.digital.v2.service.LoginService;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
public class AuthInterceptor implements HandlerInterceptor {
	
	public static List<String> authEssential = Arrays.asList("/rest/**");
	public static List<String> authInessential = Arrays.asList("/rest/person/signUp", "/rest/person/login");
	
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        final String token = request.getHeader("Authorization");
        log.info("preHandle: " + token);
        
        // token이 map에 저장되어 있지 않으면 false
        if (LoginService.tokenMap == null || LoginService.tokenMap.get(token) == null) {
        	response.setContentType("application/json");
        	response.setCharacterEncoding("UTF-8");
        	response.getWriter().write("{\"errorCode\":\"401\",\"errorMsg\":\"유효하지 않은 접근입니다.\"}");
        	return false;
        }
        
        // 유효 시간 갱신
		long currentTime = System.currentTimeMillis();
        
		Map<Long, Long> loginMap = LoginService.tokenMap.get(token);
		Set<Long> set = loginMap.keySet();
		Iterator<Long> iterator = set.iterator();
        
		loginMap.put(iterator.next(), currentTime);
		LoginService.tokenMap.put(token, loginMap);

        return true;
    }
    
}
