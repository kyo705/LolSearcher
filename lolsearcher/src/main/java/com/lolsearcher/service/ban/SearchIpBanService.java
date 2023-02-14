package com.lolsearcher.service.ban;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.stereotype.Service;

import static com.lolsearcher.constant.LolSearcherConstants.SEARCH_BAN_COUNT;
import static com.lolsearcher.constant.RedisCacheConstants.*;
import static java.util.Objects.requireNonNull;

@Slf4j
@RequiredArgsConstructor
@Service
public class SearchIpBanService implements IpBanService {

	private final CacheManager rediscCacheManager;

	@Override
	public boolean isExceedBanCount(String ipAddress) {
		Cache loginAbusingCache = rediscCacheManager.getCache(LOGIN_ABUSING_KEY);

		assert loginAbusingCache != null;

		if(loginAbusingCache.get(ipAddress) == null){
			loginAbusingCache.put(ipAddress, 1);
		}else{
			loginAbusingCache.put(ipAddress, (Integer) loginAbusingCache.get(ipAddress).get() + 1);
		}

		return (Integer) loginAbusingCache.get(ipAddress).get() >= SEARCH_BAN_COUNT;
	}

	@Override
	public void registerBanList(String ipAddress) {
		requireNonNull(rediscCacheManager.getCache(SEARCH_BAN_KEY)).put(ipAddress, System.currentTimeMillis());

		requireNonNull(rediscCacheManager.getCache(SEARCH_ABUSING_KEY)).evictIfPresent(ipAddress);
	}

}
