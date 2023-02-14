package com.lolsearcher.exception.exception.join;

public class RandomNumDifferenceException extends RuntimeException {
	private String email;
	
	public RandomNumDifferenceException() {};
	
	public RandomNumDifferenceException(String email) {
		this.email = email;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	};
	
}
