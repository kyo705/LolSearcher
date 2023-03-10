package com.lolsearcher.constant.enumeration;

import lombok.Getter;

import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Getter
public enum JoinStatus {

	OK(0),
	EXISTED(1),
	NOT_ALLOWED(2);
	
	private final int code;
	
	JoinStatus(int code) {
		this.code = code;
	}

	private static final Map<Integer, JoinStatus> BY_CODE =
			Stream.of(values()).collect(Collectors.toMap(JoinStatus::getCode, e -> e));

	public static final JoinStatus valueOfCode(int code){
		return BY_CODE.get(code);
	}
}
