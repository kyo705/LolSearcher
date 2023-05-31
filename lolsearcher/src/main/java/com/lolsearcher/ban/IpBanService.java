package com.lolsearcher.ban;

public interface IpBanService {

	void addBanCount(String ipAddress);

	boolean isExceedBanCount(String ipAddress);
}
