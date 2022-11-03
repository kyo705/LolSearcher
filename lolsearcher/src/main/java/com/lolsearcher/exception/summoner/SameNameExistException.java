package com.lolsearcher.exception.summoner;

public class SameNameExistException extends RuntimeException {

	private static final long serialVersionUID = 1L;

	@Override
	public String getMessage() {
		// TODO Auto-generated method stub
		return "same Summoner data exist. so something wrong at server logic";
	}
}
