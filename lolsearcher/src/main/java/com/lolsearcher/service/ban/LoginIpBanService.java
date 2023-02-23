package com.lolsearcher.service.ban;

import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.stereotype.Service;

import static com.lolsearcher.constant.LolSearcherConstants.LOGIN_BAN_COUNT;
import static com.lolsearcher.constant.RedisCacheNameConstants.LOGIN_BAN;

@Slf4j
@Service
public class LoginIpBanService implements IpBanService {

	private final Cache cache;

	public LoginIpBanService(CacheManager cacheManager){

		if(cacheManager.getCache(LOGIN_BAN) == null){
			log.error("로그인 차단 관련 Cache가 존재하지 않음");
			throw new IllegalArgumentException();
		}
		this.cache = cacheManager.getCache(LOGIN_BAN);
	}

	@Override
	public void addBanCount(String ipAddress) {

		cache.putIfAbsent(ipAddress, 0);
		cache.put(ipAddress, cache.get(ipAddress, Integer.class)+1);
	}

	@Override
	public boolean isExceedBanCount(String ipAddress) {

		if(cache.get(ipAddress) == null) {
			return false;
		}
		try {
			return cache.get(ipAddress, Integer.class) >= LOGIN_BAN_COUNT;
		} catch (IllegalStateException e){
			log.error("Cache value 값이 Integer 타입이 아님");
			throw new RuntimeException(e);
		}
	}
}
