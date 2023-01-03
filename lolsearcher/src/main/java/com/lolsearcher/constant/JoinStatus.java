package com.lolsearcher.constant;

public enum JoinStatus {
	OK(0), 
	
	EXISTED(1), 
	
	NOTALLOWED(2);
	
	private final int value;
	
	JoinStatus(int value) {
		this.value = value;
	}
	
	public int getValue() {
		return this.value;
	}
}
