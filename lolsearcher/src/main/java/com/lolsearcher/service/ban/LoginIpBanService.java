package com.lolsearcher.service.ban;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Service;

import com.lolsearcher.filter.LoginBanFilter;

@Service
public class LoginIpBanService implements IpBanService {

	private final Map<String, Integer> banCount = new ConcurrentHashMap<String, Integer>();
	private final ApplicationContext applicationContext;
	
	public LoginIpBanService(ApplicationContext applicationContext) {
		super();
		this.applicationContext = applicationContext;
	}

	@Override
	public boolean isExceedBanCount(int count, String ip) {
		banCount.put(ip, banCount.getOrDefault(ip, 0)+1);
		if(banCount.get(ip)>=count) {
			return true;
		}
		
		return false;
	}
	
	@Override
	public void registerBanList(String user_ip) {
		LoginBanFilter loginBanFilter = (LoginBanFilter) applicationContext.getBean("loginBanFilter");
		loginBanFilter.addBanList(user_ip);
	}

	@Override
	public void resetBanCount(String user_ip) {
		banCount.remove(user_ip);
	}

	@Override
	public void removeBanList(String user_ip) {
		LoginBanFilter loginBanFilter = (LoginBanFilter) applicationContext.getBean("loginBanFilter");
		loginBanFilter.removeBanList(user_ip);
	}
}
