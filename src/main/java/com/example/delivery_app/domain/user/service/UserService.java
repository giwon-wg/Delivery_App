package com.example.delivery_app.domain.user.service;

import com.example.delivery_app.domain.user.dto.request.LoginRequest;
import com.example.delivery_app.domain.user.dto.request.SignUpRequest;
import com.example.delivery_app.domain.user.dto.response.LoginResponse;

public interface UserService {

	void signUp(SignUpRequest request);

	LoginResponse login(LoginRequest request);
}
