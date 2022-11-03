package com.lolsearcher.service.ban;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Service;

import com.lolsearcher.filter.IpBanFilter;

@Service
public class SearchIpBanService implements IpBanService {

	private final Map<String, Integer> banCount;
	private final ApplicationContext applicationContext;
	
	public SearchIpBanService(ApplicationContext applicationContext) {
		banCount = new ConcurrentHashMap<String, Integer>();
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

	@SuppressWarnings("unchecked")
	@Override
	public void registerBanList(String user_ip) {
		FilterRegistrationBean<IpBanFilter> ipBanFilter = (FilterRegistrationBean<IpBanFilter>) applicationContext.getBean("ipBanFilter");
		ipBanFilter.getFilter().addBanList(user_ip);
	}

	@Override
	public void resetBanCount(String user_ip) {
		banCount.remove(user_ip);
	}

	@SuppressWarnings("unchecked")
	@Override
	public void removeBanList(String user_ip) {
		FilterRegistrationBean<IpBanFilter> ipBanFilter = (FilterRegistrationBean<IpBanFilter>) applicationContext.getBean("ipBanFilter");
		ipBanFilter.getFilter().removeBanList(user_ip);
	}

}
