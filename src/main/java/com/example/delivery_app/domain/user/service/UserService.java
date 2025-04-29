package com.example.delivery_app.domain.user.service;

import com.example.delivery_app.common.redis.dto.TokenRefreshRequest;
import com.example.delivery_app.common.redis.dto.TokenRefreshResponse;
import com.example.delivery_app.domain.user.Auth.UserAuth;
import com.example.delivery_app.domain.user.dto.request.LoginRequest;
import com.example.delivery_app.domain.user.dto.request.OwnerApplyRequest;
import com.example.delivery_app.domain.user.dto.request.PasswordChangeRequest;
import com.example.delivery_app.domain.user.dto.request.SignUpRequest;
import com.example.delivery_app.domain.user.dto.request.UserProfileUpdateRequest;
import com.example.delivery_app.domain.user.dto.response.LoginResponse;
import com.example.delivery_app.domain.user.dto.response.UserProfileDto;
import com.example.delivery_app.domain.user.entity.User;

import jakarta.servlet.http.HttpServletRequest;

import jakarta.servlet.http.HttpServletRequest;

public interface UserService {

	void signUp(SignUpRequest request);

	LoginResponse login(LoginRequest request);

	TokenRefreshResponse reissue(TokenRefreshRequest refreshRequest, HttpServletRequest request);

	void logout(Long userId, String accessToken);

	UserProfileDto getProfile(Long id, boolean isPrivate);

	void updateProfile(Long id, UserAuth currentUser, UserProfileUpdateRequest request);

	void changePassword(Long targetId, UserAuth currentUser, PasswordChangeRequest request);

	void deleteAccount(Long targetId, UserAuth currentUser, String token);

	void deleteAccountByAdmin(Long targetId, UserAuth currentUser);

	void applyForBusiness(OwnerApplyRequest request, UserAuth currentUser);

	User registerIfNeed(String email);

}
