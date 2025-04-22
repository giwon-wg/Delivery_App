package com.example.delivery_app.domain.user.service;

import com.example.delivery_app.domain.user.dto.SignUpRequest;

public interface UserService {
	void signUp(SignUpRequest request);
}
