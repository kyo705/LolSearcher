package com.lolsearcher.service;

import java.util.Iterator;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.springframework.stereotype.Service;

@Service
public class BanService {

	private Map<String, Integer> banCount  = new ConcurrentHashMap<>();
	private Map<String, Long> lastBadRequestTime = new ConcurrentHashMap<>();
	
	public boolean findId(String ip) {
		for(Map.Entry<String, Integer> entry : banCount.entrySet()) {
			if(entry.getKey().equals(ip)) {
				if(entry.getValue()>=20) {
					return true;
				}
			}
		}
		return false;
	}
	
	public void addBanCount(String ip) {
		banCount.put(ip, banCount.getOrDefault(ip, 0)+1);
		lastBadRequestTime.put(ip, System.currentTimeMillis());
	}

	public void removeBanCount(String id) {
		Iterator<Map.Entry<String, Integer>> iter = banCount.entrySet().iterator();
		while(iter.hasNext()) {
			Map.Entry<String, Integer> entry = iter.next();
			
			long last_time = lastBadRequestTime.get(entry.getKey());
			long cur_time = System.currentTimeMillis();
			
			if(cur_time - last_time>= 24*60*60*1000) {
				lastBadRequestTime.remove(entry.getKey());
				iter.remove();
			}
		}
	}
}
