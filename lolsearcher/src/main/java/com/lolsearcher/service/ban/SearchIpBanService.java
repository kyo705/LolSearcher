package com.lolsearcher.service.ban;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.stereotype.Service;

import static com.lolsearcher.constant.LolSearcherConstants.SEARCH_BAN_COUNT;
import static com.lolsearcher.constant.CacheConstants.*;
import static java.util.Objects.requireNonNull;

@Slf4j
@RequiredArgsConstructor
@Service
public class SearchIpBanService implements IpBanService {

	private final CacheManager rediscCacheManager;

	@Override
	public boolean isExceedBanCount(String ip) {
		Cache loginAbusingCache = rediscCacheManager.getCache(LOGIN_ABUSING_KEY);

		assert loginAbusingCache != null;

		if(loginAbusingCache.get(ip) == null){
			loginAbusingCache.put(ip, 1);
		}else{
			loginAbusingCache.put(ip, (Integer) loginAbusingCache.get(ip).get() + 1);
		}

		return (Integer) loginAbusingCache.get(ip).get() >= SEARCH_BAN_COUNT;
	}

	@Override
	public void registerBanList(String user_ip) {
		requireNonNull(rediscCacheManager.getCache(SEARCH_BAN_KEY)).put(user_ip, System.currentTimeMillis());

		requireNonNull(rediscCacheManager.getCache(SEARCH_ABUSING_KEY)).evictIfPresent(user_ip);
	}

}
