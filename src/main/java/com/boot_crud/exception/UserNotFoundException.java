package com.boot_crud.exception;

public class UserNotFoundException extends Throwable {
	public UserNotFoundException(String message) {
		super(message);
	}
}
