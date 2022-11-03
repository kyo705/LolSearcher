package com.lolsearcher.service.ban;

import org.springframework.stereotype.Service;

@Service
public interface IpBanService {

	public boolean isExceedBanCount(int count, String ip);
	
	public void registerBanList(String user_ip);
	
	public void resetBanCount(String user_ip);
	
	public void removeBanList(String user_ip);
}
