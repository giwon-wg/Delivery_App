package com.example.delivery_app.common.dto;

import org.springframework.http.HttpStatus;

public interface ResponseCode {
	HttpStatus getHttpStatus();

	String getMessage();
}
