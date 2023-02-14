package com.lolsearcher.service.ban;

public interface IpBanService {

	boolean isExceedBanCount(String ipAddress);
	
	void registerBanList(String ipAddress);
}
