package com.lolsearcher.service.ban;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import com.lolsearcher.constant.BanConstants;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import com.lolsearcher.filter.LoginBanFilter;
import com.lolsearcher.scheduler.dto.Timer;
import com.lolsearcher.scheduler.job.RemovingBannedIpJob;
import com.lolsearcher.scheduler.service.SchedulerService;

@Slf4j
@RequiredArgsConstructor
@Service
public class LoginIpBanService implements IpBanService {

	private final Map<String, Integer> banCount = new ConcurrentHashMap<>();
	
	private final SchedulerService schedulerService;
	private final LoginBanFilter loginBanFilter;

	@Override
	public boolean isExceedBanCount(String ip) {
		banCount.put(ip, banCount.getOrDefault(ip, 0)+1);

		if(banCount.get(ip)>= BanConstants.LOGIN_BAN_COUNT) {
			return true;
		}
		return false;
	}
	
	@Override
	public void registerBanList(String user_ip) {
		loginBanFilter.addBanList(user_ip);
		
		Timer timer = new Timer();
		timer.setCallbackData(user_ip);
		timer.setInitialOffsetMs(1000*60*10); //10분
		timer.setRepeatIntervalMs(0);
		timer.setRunForever(false);
		timer.setTotalFireCount(1);

		log.info("스레드 : {} 에서 실행", Thread.currentThread());
		schedulerService.schedule(RemovingBannedIpJob.class, timer);
	}

	@Override
	public void resetBanCount(String user_ip) {
		banCount.remove(user_ip);
	}

	@Override
	public void removeBanList(String user_ip) {
		log.info("IP : '{}' 벤 목록에서 삭제 시도", user_ip);
		loginBanFilter.removeBanList(user_ip);
		log.info("IP : '{}' 벤 목록에서 삭제 성공", user_ip);
	}
}
