package com.lolsearcher.service.ban;

import org.springframework.stereotype.Service;

@Service
public interface IpBanService {

	boolean isExceedBanCount(String ip);
	
	void registerBanList(String user_ip);
}
