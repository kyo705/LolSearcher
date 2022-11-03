package com.lolsearcher.service.ban;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Service;

import com.lolsearcher.filter.LoginBanFilter;
import com.lolsearcher.scheduler.dto.Timer;
import com.lolsearcher.scheduler.job.RemovingBannedIpJob;
import com.lolsearcher.scheduler.service.SchedulerService;

@Service
public class LoginIpBanService implements IpBanService {
	private final Logger logger = LoggerFactory.getLogger(this.getClass());
	private final Map<String, Integer> banCount = new ConcurrentHashMap<String, Integer>();
	private final ApplicationContext applicationContext;
	SchedulerService schedulerService;
	
	public LoginIpBanService(ApplicationContext applicationContext, SchedulerService schedulerService) {
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
	
	@Override
	public void registerBanList(String user_ip) {
		LoginBanFilter loginBanFilter = (LoginBanFilter) applicationContext.getBean("loginBanFilter");
		loginBanFilter.addBanList(user_ip);
		
		Timer timer = new Timer();
		timer.setCallbackData(user_ip);
		timer.setInitialOffsetMs(1000*60*10); //10분
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

	@Override
	public void removeBanList(String user_ip) {
		logger.info("IP : '{}' 벤 목록에서 삭제 시도", user_ip);
		LoginBanFilter loginBanFilter = (LoginBanFilter) applicationContext.getBean("loginBanFilter");
		loginBanFilter.removeBanList(user_ip);
		logger.info("IP : '{}' 벤 목록에서 삭제 성공", user_ip);
	}
}
