package com.lolsearcher.exception.summoner;

public class SameValueExistException extends RuntimeException {

	private static final long serialVersionUID = 1L;

	@Override
	public String getMessage() {
		return "same Summoner data exist. so something wrong at server logic";
	}
}
