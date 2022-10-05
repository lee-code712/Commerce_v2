package com.digital.v2.service;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import org.springframework.stereotype.Component;

@Component
public class LoginService {

	private static Map<String, Map<Long, Long>> tokenMap;
	
	public static Map<Long, Long> getLoginMap (String token)  {
		Map<Long, Long> loginMap = tokenMap.get(token);
		return loginMap;
	}
	
	public synchronized static void setLoginMap (String token, Map<Long, Long> loginMap)  {
		tokenMap.put(token, loginMap);
	}
	
	public synchronized String setToken (long personId) throws Exception {
		String token = "Auth " + System.currentTimeMillis();
		
		if (tokenMap == null) {
			tokenMap = new HashMap<String, Map<Long, Long>>();
		}
		
		Map<Long, Long> loginMap = new HashMap<>();
	
		long currentTime = System.currentTimeMillis();
		loginMap.put(personId, currentTime);
		
		tokenMap.put(token, loginMap);
		
		return token;
	}

	public long getPersonId (String token) {
		Map<Long, Long> loginMap = tokenMap.get(token);
		Set<Long> set = loginMap.keySet();
		Iterator<Long> iterator = set.iterator();
		
		if (iterator.hasNext()) {
			return iterator.next();
		}
		return 0;
	}
	
	public synchronized void deleteToken (String token) {
		tokenMap.remove(token);
	}
		
}
