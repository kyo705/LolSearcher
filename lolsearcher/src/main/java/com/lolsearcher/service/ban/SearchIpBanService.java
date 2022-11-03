package com.lolsearcher.service.ban;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Service;

import com.lolsearcher.filter.IpBanFilter;
import com.lolsearcher.scheduler.dto.Timer;
import com.lolsearcher.scheduler.job.RemovingBannedIpJob;
import com.lolsearcher.scheduler.service.SchedulerService;

@Service
public class SearchIpBanService implements IpBanService {
	private final Logger logger = LoggerFactory.getLogger(this.getClass());
	private final Map<String, Integer> banCount;
	private final ApplicationContext applicationContext;
	SchedulerService schedulerService;
	
	public SearchIpBanService(ApplicationContext applicationContext, SchedulerService schedulerService) {
		banCount = new ConcurrentHashMap<String, Integer>();
		this.applicationContext = applicationContext;
		this.schedulerService = schedulerService;
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
		
		Timer timer = new Timer();
		timer.setCallbackData(user_ip);
		timer.setInitialOffsetMs(1000*60*60*24); //24시간
		timer.setRepeatIntervalMs(0);
		timer.setRunForever(false);
		timer.setTotalFireCount(1);
		
		logger.info("스레드 : {} 에서 실행", Thread.currentThread());
		schedulerService.schedule(RemovingBannedIpJob.class, timer);
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
