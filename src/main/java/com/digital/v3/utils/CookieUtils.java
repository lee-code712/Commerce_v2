package com.digital.v3.utils;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.util.List;
import java.util.ArrayList;
import java.util.Arrays;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class CookieUtils {
	
	private static final String encoding = "UTF-8";
	private static final String path = "/";

	// 구분자(,)를 기준으로 cookie 값을 리스트에 담아서 반환
	public static List<String> getValueList (String key, HttpServletRequest request) 
			throws UnsupportedEncodingException {
		
		Cookie[] cookies = request.getCookies();
		
		// cookie 배열에서 key에 해당하는 cookie 값 가져오기
		String[] cookieValues = null;
		if (cookies != null) {
			for (Cookie cookie : cookies) {
				if (cookie.getName().equals(key)) {
					String value = cookie.getValue();
					cookieValues = (URLDecoder.decode(value, encoding)).split(",");	// 구분자를 기준으로 나눈 값을 배열에 저장
					break;
				}
			}
		}

		// 배열을 리스트로 변환
		List<String> valueList = null;
		if (cookieValues != null) {
			valueList = new ArrayList<String>(Arrays.asList(cookieValues));
		}
		
		return valueList;
	}

	// 쿠키에서 특정 부분 값 추가
	public static void setCookieValue (String key, String newValue, int day, HttpServletRequest request, 
			HttpServletResponse response) throws UnsupportedEncodingException {
		
		List<String> valueList = getValueList(key, request);
		
		// value를 추가한 cookie 값 set
		String resultValue = "";
		if (valueList != null) {
			for (String value : valueList) {
				resultValue += value + ",";
			}
			resultValue += newValue;
		} else {
			resultValue = newValue;
		}

		if (!resultValue.equals("")) {	// 다시 set한 cookie 값이 비어있지 않으면 result value로 cookie 재생성
			createCookie(key, resultValue, 1, request, response);
		}
	}

	// 쿠키에서 특정 부분 값 삭제
	public static void deleteCookieValue (String key, String deleteValue, HttpServletRequest request, 
			HttpServletResponse response) throws UnsupportedEncodingException {
		
		List<String> valueList = getValueList(key, request);
		
		// value를 포함하는 list 값 제거
		for(String value : valueList) {
			if (value.contains(deleteValue)) {
				valueList.remove(value);
				break;
			}
		}

		// value를 제외한 cookie 값 set
		String resultValue = "";
		if (valueList.size() != 0) {
			for (String value : valueList) {
				resultValue += value + ",";
			}
			if (resultValue.substring(resultValue.length() - 1).equals(",")) {
				resultValue = (resultValue.substring(0, resultValue.length() - 1)).replaceAll(" ", "");
			}
		}

		if (resultValue.equals("")) {	// 다시 set한 cookie 값이 비었으면 cookie 삭제
			deleteCookie(key, response);
		}
		else {
			createCookie (key, resultValue, 1, request, response);	// 비어있지 않으면 result value로 cookie 재생성
		}
	}

	// 새로운 쿠키 생성
	public static void createCookie (String key, String value, int day, HttpServletRequest request,
			HttpServletResponse response) throws UnsupportedEncodingException {
		
		Cookie cookie = new Cookie(key, URLEncoder.encode(value, encoding));
		cookie.setPath(path);
		cookie.setMaxAge(60 * 60 * 24 * day);
		response.addCookie(cookie);
	}
	
	// 특정 쿠키 삭제
	public static void deleteCookie (String key, HttpServletResponse response) 
			throws UnsupportedEncodingException {
		
	    Cookie cookie = new Cookie(key, "");
	    cookie.setPath(path);
	    cookie.setMaxAge(0);
	    response.addCookie(cookie);
	}
	
	// 모든 쿠키 삭제
	public static void deleteAllCookie (HttpServletRequest request, HttpServletResponse response) 
			throws UnsupportedEncodingException {
		
	    Cookie[] cookies = request.getCookies();
	    if (cookies != null) {
	        for (int i = 0; i < cookies.length; i++) {
	        	cookies[i].setValue("");
	        	cookies[i].setPath(path);
	            cookies[i].setMaxAge(0);
	            response.addCookie(cookies[i]);
	        }
	    }
	}

}
