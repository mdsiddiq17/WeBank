package com.bank.exception;

import com.bank.enums.Errors;

public class BankException extends Exception{

	private static final long serialVersionUID = 1L;
	public BankException(String message,Throwable throwable) {
		super(message,throwable);
	}
	public BankException(String message) {
		super(message);
	}
	public BankException(Errors wrongPassword) {
		super();
	}
}
