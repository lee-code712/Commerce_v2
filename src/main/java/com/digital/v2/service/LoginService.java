package com.digital.v2.service;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
public class LoginService {

	public static Map<String, Map<Long, Long>> tokenMap;
	
	public String setToken (long personId) throws Exception {
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
	
	public Map<Long, Long> getLoginMap (String token) {
		Map<Long, Long> loginMap = tokenMap.get(token);
		return loginMap;
	}
	
	public long getLoginValue (String token) {
		Map<Long, Long> loginMap = tokenMap.get(token);
		Set<Long> set = loginMap.keySet();
		Iterator<Long> iterator = set.iterator();
		
		return iterator.next();
	}
	
	public void deleteToken (String token) {
		tokenMap.remove(token);
	}

	@Scheduled(cron = "0 * * * * *")
	public void tokenValidTimeScheduler() {	
		System.out.println("스케줄러 실행");
		
		if (tokenMap != null) {
			Set<String> set = tokenMap.keySet();
			Iterator<String> iterator = set.iterator();
	
			// token map에 있는 모든 token valid time을 확인해 30분을 넘으면 토큰 삭제
			while (iterator.hasNext()) {
				String key = iterator.next();
				Map<Long, Long> loginMap = tokenMap.get(key);
				
				Set<Long> subSet = loginMap.keySet();
				Iterator<Long> subIterator = subSet.iterator();
				
				long start = loginMap.get(subIterator.next());
				long currentTime = System.currentTimeMillis();
				long elapse = currentTime - start;
	
				if (elapse > 30 * 60 * 1000) {
					deleteToken(key);
					System.out.println(key + " 토큰 삭제");
				}
			}
		}
	}
		
}
