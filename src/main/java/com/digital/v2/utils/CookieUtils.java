package com.digital.v2.utils;

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

	/**
	 * @description 특정 key의 쿠키값을 List로 반환한다.
	 * @params key: 쿠키 이름
	 */
	public static List<String> getValueList(String key, HttpServletRequest request) throws UnsupportedEncodingException {
		Cookie[] cookies = request.getCookies();
		String[] cookieValues = null;
		String value = "";
		List<String> list = null;

		// 특정 key의 쿠키값을 ","로 구분하여 String 배열에 담아준다.
		// ex) 쿠키의 key/value ---> key = "clickItems", value = "211, 223, 303"(상품 번호)
		if (cookies != null) {
			for (int i = 0; i < cookies.length; i++) {
				if (cookies[i].getName().equals(key)) {
					value = cookies[i].getValue();
					cookieValues = (URLDecoder.decode(value, encoding)).split(",");
					break;
				}
			}
		}

		// String 배열에 담겼던 값들을 List로 다시 담는다.
		if (cookieValues != null) {
			list = new ArrayList<String>(Arrays.asList(cookieValues));

			if (list.size() > 3) { // 값이 3개를 초과하면, 최근 것 3개만 담는다.
				int start = list.size() - 3;
				List<String> copyList = new ArrayList<String>();
				for (int i = start; i < list.size(); i++) {
					copyList.add(list.get(i));
				}
				list = copyList;
			}
		}
		return list;
	}

	/**
	 * @description 쿠키를 생성 혹은 업데이트한다.
	 * @params key: 쿠키 이름, value: 쿠키 이름과 짝을 이루는 값, day: 쿠키의 수명
	 */
	public static void setCookieValue(String key, String value, int day, HttpServletRequest request, HttpServletResponse response)
			throws UnsupportedEncodingException {
		List<String> list = getValueList(key, request);
		String sumValue = "";
		int equalsValueCnt = 0;

		if (list != null) {
			for (int i = 0; i < list.size(); i++) {
				sumValue += list.get(i) + ",";
				if (list.get(i).equals(value)) {
					equalsValueCnt++;
				}
			}
			if (equalsValueCnt != 0) { // 같은 값을 넣으려고 할 때의 처리
				if (sumValue.substring(sumValue.length() - 1).equals(",")) {
					sumValue = sumValue.substring(0, sumValue.length() - 1);
				}
			} else {
				sumValue += value;
			}
		} else {
			sumValue = value;
		}

		if (!sumValue.equals("")) {
			createCookie(key, sumValue, 1, request, response);
		}
	}

	/**
	 * @description 쿠키값들 중 특정 값을 삭제한다.
	 * @params key: 쿠키 이름, value: 쿠키 이름과 짝을 이루는 값
	 */
	public static void deleteCookieValue(String key, String value, HttpServletRequest request, HttpServletResponse response)
			throws UnsupportedEncodingException {
		List<String> list = getValueList(key, request);
//		list.remove(value);
		
		for(String listValue : list) {
			if (listValue.startsWith(value)) {
				list.remove(listValue);
				break;
			}
		}

		String sumValue = "";
		if (list.size() != 0) {
			for (int i = 0; i < list.size(); i++) {
				sumValue += list.get(i) + ",";
			}
			if (sumValue.substring(sumValue.length() - 1).equals(",")) {
				sumValue = (sumValue.substring(0, sumValue.length() - 1)).replaceAll(" ", "");
			}
		}

		if (sumValue.equals("")) {
			deleteCookie(key, response);
		}
		else {
			createCookie (key, sumValue, 1, request, response);
		}
	}

	// 쿠키 생성
	public static void createCookie (String key, String value, int day, HttpServletRequest request,
			HttpServletResponse response) throws UnsupportedEncodingException {
		Cookie cookie = new Cookie(key, URLEncoder.encode(value, encoding));
		cookie.setPath(path);
		cookie.setMaxAge(60 * 60 * 24 * day);
		response.addCookie(cookie);
	}
	
	// 쿠키 삭제
	public static void deleteCookie (String key, HttpServletResponse response) throws UnsupportedEncodingException {
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
