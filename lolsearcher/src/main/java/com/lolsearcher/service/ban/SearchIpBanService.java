package com.lolsearcher.service.ban;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.stereotype.Service;

import static com.lolsearcher.constant.LolSearcherConstants.SEARCH_BAN_COUNT;
import static com.lolsearcher.constant.RedisCacheNameConstants.SEARCH_BAN;

@Slf4j
@RequiredArgsConstructor
@Service
public class SearchIpBanService implements IpBanService {

	private final CacheManager cacheManager;

	@Override
	public void addBanCount(String ipAddress) {

		Cache cache = cacheManager.getCache(SEARCH_BAN);
		assert cache != null;

		cache.putIfAbsent(ipAddress, 0);
		cache.put(ipAddress, cache.get(ipAddress, Integer.class) + 1);
	}

	@Override
	public boolean isExceedBanCount(String ipAddress) {

		Cache cache = cacheManager.getCache(SEARCH_BAN);
		assert cache != null;

		if(cache.get(ipAddress) == null) {
			return false;
		}
		try {
			return cache.get(ipAddress, Integer.class) >= SEARCH_BAN_COUNT;
		} catch (IllegalStateException e){
			log.error("Cache value 값이 Integer 타입이 아님");
			throw new RuntimeException(e);
		}
	}

}
