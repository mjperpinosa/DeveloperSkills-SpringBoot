package com.devs.rest.devsrest.exceptions;

public class NoListFoundException extends Exception {
	
	private static final long serialVersionUID = 1L;

	public NoListFoundException(String message) {
		super(message);
	}
	
	public NoListFoundException(String message, Throwable cause) {
		super(message, cause);
	}
}
