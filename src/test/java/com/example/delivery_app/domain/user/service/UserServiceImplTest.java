package com.example.delivery_app.domain.user.service;

import com.example.delivery_app.common.exception.CustomException;
import com.example.delivery_app.common.jwt.JwtTokenProvider;
import com.example.delivery_app.common.redis.dto.TokenRefreshRequest;
import com.example.delivery_app.common.redis.dto.TokenRefreshResponse;
import com.example.delivery_app.common.redis.service.BlackListService;
import com.example.delivery_app.common.redis.service.RefreshTokenService;
import com.example.delivery_app.domain.user.Auth.UserAuth;
import com.example.delivery_app.domain.user.dto.request.LoginRequest;
import com.example.delivery_app.domain.user.dto.request.OwnerApplyRequest;
import com.example.delivery_app.domain.user.dto.request.PasswordChangeRequest;
import com.example.delivery_app.domain.user.dto.request.SignUpRequest;
import com.example.delivery_app.domain.user.dto.request.UserProfileUpdateRequest;
import com.example.delivery_app.domain.user.dto.response.LoginResponse;
import com.example.delivery_app.domain.user.dto.response.UserProfileDto;
import com.example.delivery_app.domain.user.entity.User;
import com.example.delivery_app.domain.user.entity.UserRole;
import com.example.delivery_app.domain.user.exception.AuthErrorCode;
import com.example.delivery_app.domain.user.exception.UserErrorCode;
import com.example.delivery_app.domain.user.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.util.ReflectionTestUtils;


import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.*;

import java.util.Date;
import java.util.List;
import java.util.Optional;

import io.jsonwebtoken.Jwts;

public class UserServiceImplTest {

	private User testUser;

	private String accessToken;

	private String refreshToken;

	@InjectMocks
	private UserServiceImpl userService;

	@Mock
	private UserRepository userRepository;

	@Mock
	private PasswordEncoder passwordEncoder;

	@Mock
	private JwtTokenProvider jwtTokenProvider;

	@Mock
	private RefreshTokenService refreshTokenService;

	@Mock
	private RedisTemplate<String, String> redisTemplate;

	@Mock
	private BlackListService blackListService;

	@Mock
	private ValueOperations<String, String> valueOperations;

	@BeforeEach
	void setUp() {
		MockitoAnnotations.openMocks(this);

		testUser = User.builder()
			.email("test@deliveryhajo.com")
			.password("encodedPassword")
			.nickname("nickname")
			.role(UserRole.USER)
			.address("test address")
			.isDeleted(false)
			.build();
		ReflectionTestUtils.setField(testUser, "id", 1L);

		given(redisTemplate.opsForValue()).willReturn(valueOperations);
	}

	//회원 가입
	@Test
	void 회원가입성공() {
		// given
		SignUpRequest request = new SignUpRequest("test@deliveryhajo.com",
			"password",
			"nickname",
			"address");

		given(userRepository.existsByEmailAndIsDeletedFalse(anyString())).willReturn(false);
		given(userRepository.existsByNicknameAndIsDeletedFalse(anyString())).willReturn(false);
		given(passwordEncoder.encode(anyString())).willReturn("encodedPassword");
		given(userRepository.save(any(User.class))).willAnswer(invocation -> invocation.getArgument(0));

		// when
		userService.signUp(request);

		// then
		verify(userRepository).save(any(User.class));
	}

	@Test
	void 회원가입실패_이메일중복() {
		// given
		SignUpRequest request = new SignUpRequest(
			"test@deliveryhajo.com",
			"password",
			"nickname",
			"address");
		given(userRepository.existsByEmailAndIsDeletedFalse(anyString())).willReturn(true);

		// when
		Throwable thrown = catchThrowable(() -> userService.signUp(request));

		// then
		assertThat(thrown)
			.isInstanceOf(CustomException.class)
			.satisfies(e -> {
				CustomException ce = (CustomException) e;
				assertThat(ce.getResponseCode()).isEqualTo(UserErrorCode.DUPLICATE_EMAIL);
			});
	}

	@Test
	void 회원가입실패_닉네임중복() {
		// given
		SignUpRequest request = new SignUpRequest(
			"test@deliveryhajo.com",
			"password",
			"nickname",
			"address");

		given(userRepository.existsByEmailAndIsDeletedFalse(anyString())).willReturn(false);
		given(userRepository.existsByNicknameAndIsDeletedFalse(anyString())).willReturn(true);

		// when
		Throwable thrown = catchThrowable(() -> userService.signUp(request));

		// then
		assertThat(thrown)
			.isInstanceOf(CustomException.class)
			.satisfies(e -> {
				CustomException ce = (CustomException) e;
				assertThat(ce.getResponseCode()).isEqualTo(UserErrorCode.DUPLICATE_NICKNAME);
			});
	}

	//로그인
	@Test
	void 로그인_성공() {
		// given
		LoginRequest request = new LoginRequest(
			"test@deliveryhajo.com",
			"password");

		given(userRepository.findByEmail(anyString())).willReturn(Optional.of(testUser));
		given(passwordEncoder.matches(anyString(), anyString())).willReturn(true);
		given(jwtTokenProvider.generateAccessToken(anyLong(), anyList())).willReturn("mockAccessToken");
		given(jwtTokenProvider.generateRefreshToken(anyLong())).willReturn("mockRefreshToken");

		// when
		LoginResponse response = userService.login(request);

		// then
		assertThat(response.getAccessToken()).isEqualTo("mockAccessToken");
		assertThat(response.getRefreshToken()).isEqualTo("mockRefreshToken");
	}

	@Test
	void 로그인_실패_이메일없음() {
		// given
		LoginRequest request = new LoginRequest(
			"nonexistent@deliveryhajo.com",
			"password");
		given(userRepository.findByEmail(anyString())).willReturn(Optional.empty());

		// when
		Throwable thrown = catchThrowable(() -> userService.login(request));

		// then
		assertThat(thrown)
			.isInstanceOf(CustomException.class)
			.satisfies(e -> {
				CustomException ce = (CustomException) e;
				assertThat(ce.getResponseCode()).isEqualTo(UserErrorCode.EMAIL_NOT_FOUND);
			});
	}

	@Test
	void 로그인_실패_비밀번호불일치() {
		// given
		LoginRequest request = new LoginRequest(
			"test@deliveryhajo.com",
			"wrongPassword");


		given(userRepository.findByEmail(anyString())).willReturn(Optional.of(testUser));
		given(passwordEncoder.matches(anyString(), anyString())).willReturn(false); // 패스워드 틀림

		// when
		Throwable thrown = catchThrowable(() -> userService.login(request));

		// then
		assertThat(thrown)
			.isInstanceOf(CustomException.class)
			.satisfies(e -> {
				CustomException ce = (CustomException) e;
				assertThat(ce.getResponseCode()).isEqualTo(UserErrorCode.PASSWORD_MISMATCH);
			});
	}

	//리프레쉬
	@Test
	void 리프레시_성공() {
		//give
		TokenRefreshRequest request = new TokenRefreshRequest("validRefreshToken");

		given(jwtTokenProvider.validateToken(anyString())).willReturn(true);
		given(jwtTokenProvider.getClaims(anyString())).willReturn(Jwts.claims().setSubject("1"));
		given(refreshTokenService.get(anyLong())).willReturn("validRefreshToken");
		given(userRepository.findActiveById(anyLong())).willReturn(Optional.of(testUser));
		given(jwtTokenProvider.generateAccessToken(anyLong(), anyList())).willReturn("newAccessToken");
		given(jwtTokenProvider.generateRefreshToken(anyLong())).willReturn("newRefreshToken");

		// when
		TokenRefreshResponse response = userService.reissue(request, new MockHttpServletRequest());

		// then
		assertThat(response.getAccessToken()).isEqualTo("newAccessToken");
		assertThat(response.getRefreshToken()).isEqualTo("newRefreshToken");
	}

	@Test
	void 리프레시_실패_토큰_null() {
		// given
		TokenRefreshRequest request = new TokenRefreshRequest(null);

		// when
		Throwable thrown = catchThrowable(() -> userService.reissue(request, new MockHttpServletRequest()));

		// then
		assertThat(thrown)
			.isInstanceOf(CustomException.class)
			.satisfies(e -> {
				CustomException ce = (CustomException) e;
				assertThat(ce.getResponseCode()).isEqualTo(AuthErrorCode.NULL_REFRESH_TOKEN);
			});
	}

	@Test
	void 리프레시_실패_유효하지않은_토큰() {
		// given
		TokenRefreshRequest request = new TokenRefreshRequest("invalidRefreshToken");
		given(jwtTokenProvider.validateToken(anyString())).willReturn(false);

		// when
		Throwable thrown = catchThrowable(() -> userService.reissue(request, new MockHttpServletRequest()));

		// then
		assertThat(thrown)
			.isInstanceOf(CustomException.class)
			.satisfies(e -> {
				CustomException ce = (CustomException) e;
				assertThat(ce.getResponseCode()).isEqualTo(AuthErrorCode.INVALID_REFRESH_TOKEN);
			});
	}

	@Test
	void 리프레시_실패_토큰값_불일치() {
		// given
		TokenRefreshRequest request = new TokenRefreshRequest("validRefreshToken");

		given(jwtTokenProvider.validateToken(anyString())).willReturn(true);
		given(jwtTokenProvider.getClaims(anyString())).willReturn(Jwts.claims().setSubject("1"));
		given(refreshTokenService.get(anyLong())).willReturn("otherRefreshToken");

		// when
		Throwable thrown = catchThrowable(() -> userService.reissue(request, new MockHttpServletRequest()));

		// then
		assertThat(thrown)
			.isInstanceOf(CustomException.class)
			.satisfies(e -> {
				CustomException ce = (CustomException) e;
				assertThat(ce.getResponseCode()).isEqualTo(AuthErrorCode.INVALID_REFRESH_TOKEN);
			});
	}

	@Test
	void 리프레시_실패_유저_없음() {
		// given
		TokenRefreshRequest request = new TokenRefreshRequest("validRefreshToken");

		given(jwtTokenProvider.validateToken(anyString())).willReturn(true);
		given(jwtTokenProvider.getClaims(anyString())).willReturn(Jwts.claims().setSubject("1"));
		given(refreshTokenService.get(anyLong())).willReturn("validRefreshToken");
		given(userRepository.findActiveById(anyLong())).willReturn(Optional.empty());

		// when
		Throwable thrown = catchThrowable(() -> userService.reissue(request, new MockHttpServletRequest()));

		// then
		assertThat(thrown)
			.isInstanceOf(CustomException.class)
			.satisfies(e -> {
				CustomException ce = (CustomException) e;
				assertThat(ce.getResponseCode()).isEqualTo(UserErrorCode.USER_NOT_FOUND);
			});
	}

	@Test
	void 리프레시_실패_룰_없음() {
		// given
		TokenRefreshRequest request = new TokenRefreshRequest("validRefreshToken");
		User noRoleUser = User.builder()
			.email("noRole@deliveryhajo.com")
			.password("encodedPassword")
			.nickname("nickname")
			.role(null) // role 없음
			.address("test address")
			.isDeleted(false)
			.build();
		ReflectionTestUtils.setField(noRoleUser, "id", 1L);

		given(jwtTokenProvider.validateToken(anyString())).willReturn(true);
		given(jwtTokenProvider.getClaims(anyString())).willReturn(Jwts.claims().setSubject("1"));
		given(refreshTokenService.get(anyLong())).willReturn("validRefreshToken");
		given(userRepository.findActiveById(anyLong())).willReturn(Optional.of(noRoleUser));

		// when
		Throwable thrown = catchThrowable(() -> userService.reissue(request, new MockHttpServletRequest()));

		// then
		assertThat(thrown)
			.isInstanceOf(CustomException.class)
			.satisfies(e -> {
				CustomException ce = (CustomException) e;
				assertThat(ce.getResponseCode()).isEqualTo(UserErrorCode.USER_ROLE_NOT_DEFINED);
			});
	}


	//로그아웃
	@Test
	void 로그아웃_성공() {
		// given
		String accessToken = "mockAccessToken";
		long now = System.currentTimeMillis();
		Date expiration = new Date(now + 10000);

		given(jwtTokenProvider.getExpiration(anyString())).willReturn(expiration);

		// when
		userService.logout(testUser.getId(), accessToken);

		// then
		verify(refreshTokenService).delete(testUser.getId());
		verify(blackListService).addToBlacklist(eq(accessToken), anyLong());
	}

	@Test
	void 로그아웃_성공_만료된_토큰() {
		// given
		String accessToken = "mockAccessToken";
		long now = System.currentTimeMillis();
		Date expiration = new Date(now - 10000); // 이미 만료된 토큰

		given(jwtTokenProvider.getExpiration(anyString())).willReturn(expiration);

		// when
		userService.logout(testUser.getId(), accessToken);

		// then
		verify(refreshTokenService).delete(testUser.getId());
		verify(blackListService, never()).addToBlacklist(anyString(), anyLong());
	}


	//프로필 조회
	@Test
	void 프로필_조회_사적정보() {
		// given
		given(userRepository.findActiveById(anyLong())).willReturn(Optional.of(testUser));

		// when
		UserProfileDto profile = userService.getProfile(testUser.getId(), true);

		// then
		assertThat(profile.getNickname()).isEqualTo(testUser.getNickname());
		assertThat(profile.getAddress()).isEqualTo(testUser.getAddress());
	}

	@Test
	void 프로필_조회_일부정보() {
		// given
		given(userRepository.findActiveById(anyLong())).willReturn(Optional.of(testUser));

		// when
		UserProfileDto profile = userService.getProfile(testUser.getId(), false);

		// then
		assertThat(profile.getNickname()).isEqualTo(testUser.getNickname());
		assertThat(profile.getAddress()).isNull();
	}

	@Test
	void 프로필_조회_유저없음() {
		// given
		given(userRepository.findActiveById(anyLong())).willReturn(Optional.empty());

		// when
		Throwable thrown = catchThrowable(() -> userService.getProfile(999L, true));

		// then
		assertThat(thrown)
			.isInstanceOf(CustomException.class)
			.satisfies(e -> {
				CustomException ce = (CustomException) e;
				assertThat(ce.getResponseCode()).isEqualTo(UserErrorCode.USER_NOT_FOUND);
			});
	}

	//프로필 수정
	@Test
	void 프로필_수정_본인() {
		// given
		UserAuth currentUser = UserAuth.of(testUser.getId(), List.of(UserRole.USER));
		UserProfileUpdateRequest request = new UserProfileUpdateRequest("newNickname", "newAddress");

		given(userRepository.findActiveById(anyLong())).willReturn(Optional.of(testUser));
		given(userRepository.existsByNicknameAndIsDeletedFalse(anyString())).willReturn(false);

		// when
		userService.updateProfile(testUser.getId(), currentUser, request);

		// then
		assertThat(testUser.getNickname()).isEqualTo("newNickname");
		assertThat(testUser.getAddress()).isEqualTo("newAddress");
	}

	@Test
	void 프로필_수정_어드민() {
		// given
		UserAuth adminUser = UserAuth.of(999L, List.of(UserRole.ADMIN));
		UserProfileUpdateRequest request = new UserProfileUpdateRequest("adminChangedNickname", "adminChangedAddress");

		given(userRepository.findActiveById(anyLong())).willReturn(Optional.of(testUser));
		given(userRepository.existsByNicknameAndIsDeletedFalse(anyString())).willReturn(false);

		// when
		userService.updateProfile(testUser.getId(), adminUser, request);

		// then
		assertThat(testUser.getNickname()).isEqualTo("adminChangedNickname");
		assertThat(testUser.getAddress()).isEqualTo("adminChangedAddress");
	}

	@Test
	void 프로필_수정_접근거부() {
		// given
		UserAuth otherUser = UserAuth.of(999L, List.of(UserRole.USER));
		UserProfileUpdateRequest request = new UserProfileUpdateRequest("nickname", "address");

		given(userRepository.findActiveById(anyLong())).willReturn(Optional.of(testUser));

		// when
		Throwable thrown = catchThrowable(() -> userService.updateProfile(testUser.getId(), otherUser, request));

		// then
		assertThat(thrown)
			.isInstanceOf(CustomException.class)
			.satisfies(e -> {
				CustomException ce = (CustomException) e;
				assertThat(ce.getResponseCode()).isEqualTo(UserErrorCode.ACCESS_DENIED);
			});
	}

	@Test
	void 프로필_수정_닉네임중복() {
		// given
		UserAuth currentUser = UserAuth.of(testUser.getId(), List.of(UserRole.USER));
		UserProfileUpdateRequest request = new UserProfileUpdateRequest("duplicatedNickname", "address");

		given(userRepository.findActiveById(anyLong())).willReturn(Optional.of(testUser));
		given(userRepository.existsByNicknameAndIsDeletedFalse(anyString())).willReturn(true);

		// when
		Throwable thrown = catchThrowable(() -> userService.updateProfile(testUser.getId(), currentUser, request));

		// then
		assertThat(thrown)
			.isInstanceOf(CustomException.class)
			.satisfies(e -> {
				CustomException ce = (CustomException) e;
				assertThat(ce.getResponseCode()).isEqualTo(UserErrorCode.DUPLICATE_NICKNAME);
			});
	}

	@Test
	void 프로필_수정_유저없음() {
		// given
		UserAuth currentUser = UserAuth.of(testUser.getId(), List.of(UserRole.USER));
		UserProfileUpdateRequest request = new UserProfileUpdateRequest("nickname", "address");

		given(userRepository.findActiveById(anyLong())).willReturn(Optional.empty());

		// when
		Throwable thrown = catchThrowable(() -> userService.updateProfile(testUser.getId(), currentUser, request));

		// then
		assertThat(thrown)
			.isInstanceOf(CustomException.class)
			.satisfies(e -> {
				CustomException ce = (CustomException) e;
				assertThat(ce.getResponseCode()).isEqualTo(UserErrorCode.USER_NOT_FOUND);
			});
	}

	//비밀번호 변경
	@Test
	void 비밀번호변경_본인성공() {
		// given
		UserAuth currentUser = UserAuth.of(testUser.getId(), List.of(UserRole.USER));
		PasswordChangeRequest request = new PasswordChangeRequest("encodedPassword", "newPassword", "newPassword");

		given(userRepository.findActiveById(anyLong())).willReturn(Optional.of(testUser));
		given(passwordEncoder.matches(anyString(), anyString())).willReturn(true);
		given(passwordEncoder.encode(anyString())).willReturn("encodedNewPassword");

		// when
		userService.changePassword(testUser.getId(), currentUser, request);

		// then
		assertThat(testUser.getPassword()).isEqualTo("encodedNewPassword");
	}

	@Test
	void 비밀번호변경_어드민성공() {
		// given
		UserAuth adminUser = UserAuth.of(999L, List.of(UserRole.ADMIN));
		PasswordChangeRequest request = new PasswordChangeRequest(null, "newPassword", "newPassword");

		given(userRepository.findActiveById(anyLong())).willReturn(Optional.of(testUser));
		given(passwordEncoder.encode(anyString())).willReturn("encodedNewPassword");

		// when
		userService.changePassword(testUser.getId(), adminUser, request);

		// then
		assertThat(testUser.getPassword()).isEqualTo("encodedNewPassword");
	}

	@Test
	void 비밀번호변경_현재비번틀림() {
		// given
		UserAuth currentUser = UserAuth.of(testUser.getId(), List.of(UserRole.USER));
		PasswordChangeRequest request = new PasswordChangeRequest("wrongPassword", "newPassword", "newPassword");

		given(userRepository.findActiveById(anyLong())).willReturn(Optional.of(testUser));
		given(passwordEncoder.matches(anyString(), anyString())).willReturn(false);

		// when
		Throwable thrown = catchThrowable(() -> userService.changePassword(testUser.getId(), currentUser, request));

		// then
		assertThat(thrown)
			.isInstanceOf(CustomException.class)
			.satisfies(e -> {
				CustomException ce = (CustomException) e;
				assertThat(ce.getResponseCode()).isEqualTo(UserErrorCode.PASSWORD_MISMATCH);
			});
	}

	@Test
	void 비밀번호변경_새비번불일치() {
		// given
		UserAuth currentUser = UserAuth.of(testUser.getId(), List.of(UserRole.USER));
		PasswordChangeRequest request = new PasswordChangeRequest("encodedPassword", "newPassword", "differentPassword");

		given(userRepository.findActiveById(anyLong())).willReturn(Optional.of(testUser));
		given(passwordEncoder.matches(anyString(), anyString())).willReturn(true);

		// when
		Throwable thrown = catchThrowable(() -> userService.changePassword(testUser.getId(), currentUser, request));

		// then
		assertThat(thrown)
			.isInstanceOf(CustomException.class)
			.satisfies(e -> {
				CustomException ce = (CustomException) e;
				assertThat(ce.getResponseCode()).isEqualTo(UserErrorCode.PASSWORD_CONFIRM_MISMATCH);
			});
	}

	@Test
	void 비밀번호변경_유저없음() {
		// given
		UserAuth currentUser = UserAuth.of(testUser.getId(), List.of(UserRole.USER));
		PasswordChangeRequest request = new PasswordChangeRequest("encodedPassword", "newPassword", "newPassword");

		given(userRepository.findActiveById(anyLong())).willReturn(Optional.empty());

		// when
		Throwable thrown = catchThrowable(() -> userService.changePassword(testUser.getId(), currentUser, request));

		// then
		assertThat(thrown)
			.isInstanceOf(CustomException.class)
			.satisfies(e -> {
				CustomException ce = (CustomException) e;
				assertThat(ce.getResponseCode()).isEqualTo(UserErrorCode.USER_NOT_FOUND);
			});
	}

	@Test
	void 비밀번호변경_권한없음() {
		// given
		UserAuth otherUser = UserAuth.of(999L, List.of(UserRole.USER)); // 다른 유저
		PasswordChangeRequest request = new PasswordChangeRequest("encodedPassword", "newPassword", "newPassword");

		given(userRepository.findActiveById(anyLong())).willReturn(Optional.of(testUser));

		// when
		Throwable thrown = catchThrowable(() -> userService.changePassword(testUser.getId(), otherUser, request));

		// then
		assertThat(thrown)
			.isInstanceOf(CustomException.class)
			.satisfies(e -> {
				CustomException ce = (CustomException) e;
				assertThat(ce.getResponseCode()).isEqualTo(UserErrorCode.ACCESS_DENIED);
			});
	}


	//유저 탈퇴
	@Test
	void 회원탈퇴_본인성공() {
		// given
		UserAuth currentUser = UserAuth.of(testUser.getId(), List.of(UserRole.USER));
		String token = "mockAccessToken";

		given(userRepository.findActiveById(anyLong())).willReturn(Optional.of(testUser));
		given(jwtTokenProvider.validateToken(anyString())).willReturn(true);
		given(jwtTokenProvider.getExpiration(anyString())).willReturn(new Date(System.currentTimeMillis() + 10000));

		// when
		userService.deleteAccount(testUser.getId(), currentUser, token);

		// then
		assertThat(testUser.isDeleted()).isTrue();
		verify(blackListService).addToBlacklist(eq(token), anyLong());
	}

	@Test
	void 회원탈퇴_어드민성공() {
		// given
		UserAuth adminUser = UserAuth.of(999L, List.of(UserRole.ADMIN));
		given(userRepository.findActiveById(anyLong())).willReturn(Optional.of(testUser));

		// when
		userService.deleteAccount(testUser.getId(), adminUser, null);

		// then
		assertThat(testUser.isDeleted()).isTrue();
	}

	@Test
	void 회원탈퇴_어드민자기자신삭제실패() {
		// given
		UserAuth adminUser = UserAuth.of(1L, List.of(UserRole.ADMIN));

		given(userRepository.findActiveById(anyLong())).willReturn(Optional.of(testUser));
		ReflectionTestUtils.setField(testUser, "id", 1L);

		// when
		Throwable thrown = catchThrowable(() -> userService.deleteAccount(testUser.getId(), adminUser, null));

		// then
		assertThat(thrown)
			.isInstanceOf(CustomException.class)
			.satisfies(e -> {
				CustomException ce = (CustomException) e;
				assertThat(ce.getResponseCode()).isEqualTo(UserErrorCode.ADMIN_SELF_DELETE_BLOCKED);
			});
	}

	@Test
	void 회원탈퇴_접근거부() {
		// given
		UserAuth otherUser = UserAuth.of(999L, List.of(UserRole.USER));
		given(userRepository.findActiveById(anyLong())).willReturn(Optional.of(testUser));

		// when
		Throwable thrown = catchThrowable(() -> userService.deleteAccount(testUser.getId(), otherUser, null));

		// then
		assertThat(thrown)
			.isInstanceOf(CustomException.class)
			.satisfies(e -> {
				CustomException ce = (CustomException) e;
				assertThat(ce.getResponseCode()).isEqualTo(UserErrorCode.ACCESS_DENIED);
			});
	}

	@Test
	void 회원탈퇴_유저없음() {
		// given
		UserAuth currentUser = UserAuth.of(testUser.getId(), List.of(UserRole.USER));
		given(userRepository.findActiveById(anyLong())).willReturn(Optional.empty());

		// when
		Throwable thrown = catchThrowable(() -> userService.deleteAccount(testUser.getId(), currentUser, null));

		// then
		assertThat(thrown)
			.isInstanceOf(CustomException.class)
			.satisfies(e -> {
				CustomException ce = (CustomException) e;
				assertThat(ce.getResponseCode()).isEqualTo(UserErrorCode.USER_NOT_FOUND);
			});
	}

	// 어드민탈퇴
	@Test
	void 어드민_다른유저삭제_성공() {
		// given
		UserAuth adminUser = UserAuth.of(999L, List.of(UserRole.ADMIN));
		given(userRepository.findActiveById(anyLong())).willReturn(Optional.of(testUser));

		// when
		userService.deleteAccountByAdmin(testUser.getId(), adminUser);

		// then
		assertThat(testUser.isDeleted()).isTrue();
	}

	@Test
	void 어드민_자기자신삭제_실패() {
		// given
		UserAuth adminUser = UserAuth.of(1L, List.of(UserRole.ADMIN));
		ReflectionTestUtils.setField(testUser, "id", 1L);
		given(userRepository.findActiveById(anyLong())).willReturn(Optional.of(testUser));

		// when
		Throwable thrown = catchThrowable(() -> userService.deleteAccountByAdmin(testUser.getId(), adminUser));

		// then
		assertThat(thrown)
			.isInstanceOf(CustomException.class)
			.satisfies(e -> {
				CustomException ce = (CustomException) e;
				assertThat(ce.getResponseCode()).isEqualTo(UserErrorCode.ADMIN_SELF_DELETE_BLOCKED);
			});
	}

	@Test
	void 어드민이아닌_다른유저삭제_실패() {
		// given
		UserAuth otherUser = UserAuth.of(999L, List.of(UserRole.USER));
		given(userRepository.findActiveById(anyLong())).willReturn(Optional.of(testUser));

		// when
		Throwable thrown = catchThrowable(() -> userService.deleteAccountByAdmin(testUser.getId(), otherUser));

		// then
		assertThat(thrown)
			.isInstanceOf(CustomException.class)
			.satisfies(e -> {
				CustomException ce = (CustomException) e;
				assertThat(ce.getResponseCode()).isEqualTo(UserErrorCode.ACCESS_DENIED);
			});
	}

	@Test
	void 어드민삭제_유저없음() {
		// given
		UserAuth adminUser = UserAuth.of(999L, List.of(UserRole.ADMIN));
		given(userRepository.findActiveById(anyLong())).willReturn(Optional.empty());

		// when
		Throwable thrown = catchThrowable(() -> userService.deleteAccountByAdmin(999L, adminUser));

		// then
		assertThat(thrown)
			.isInstanceOf(CustomException.class)
			.satisfies(e -> {
				CustomException ce = (CustomException) e;
				assertThat(ce.getResponseCode()).isEqualTo(UserErrorCode.ADMIN_SELF_DELETE_BLOCKED);
			});
	}

	//사업자 등록
	@Test
	void 사업자신청_성공() {
		// given
		UserAuth currentUser = UserAuth.of(testUser.getId(), List.of(UserRole.USER));
		OwnerApplyRequest request = new OwnerApplyRequest("사장님", "123-45-67890");

		given(userRepository.findActiveById(anyLong())).willReturn(Optional.of(testUser));

		// when
		userService.applyForBusiness(request, currentUser);

		// then
		assertThat(testUser.getRole()).isEqualTo(UserRole.OWNER);
	}

	@Test
	void 사업자신청_이미사장() {
		// given
		User ownerUser = User.builder()
			.email("owner@deliveryhajo.com")
			.password("encodedPassword")
			.nickname("사장님")
			.role(UserRole.OWNER)
			.address("address")
			.isDeleted(false)
			.build();
		ReflectionTestUtils.setField(ownerUser, "id", 2L);

		UserAuth currentUser = UserAuth.of(ownerUser.getId(), List.of(UserRole.OWNER));
		OwnerApplyRequest request = new OwnerApplyRequest("사장님", "123-45-67890");

		given(userRepository.findActiveById(anyLong())).willReturn(Optional.of(ownerUser));

		// when
		Throwable thrown = catchThrowable(() -> userService.applyForBusiness(request, currentUser));

		// then
		assertThat(thrown)
			.isInstanceOf(CustomException.class)
			.satisfies(e -> {
				CustomException ce = (CustomException) e;
				assertThat(ce.getResponseCode()).isEqualTo(UserErrorCode.USER_ALREADY_OWNER);
			});
	}

	@Test
	void 사업자신청_유저없음() {
		// given
		UserAuth currentUser = UserAuth.of(999L, List.of(UserRole.USER));
		OwnerApplyRequest request = new OwnerApplyRequest("사장님", "123-45-67890");

		given(userRepository.findActiveById(anyLong())).willReturn(Optional.empty());

		// when
		Throwable thrown = catchThrowable(() -> userService.applyForBusiness(request, currentUser));

		// then
		assertThat(thrown)
			.isInstanceOf(CustomException.class)
			.satisfies(e -> {
				CustomException ce = (CustomException) e;
				assertThat(ce.getResponseCode()).isEqualTo(UserErrorCode.USER_NOT_FOUND);
			});
	}

	//소셜 로그인
	@Test
	void 소셜로그인_기존회원() {
		// given
		String email = "existing@social.com";
		given(userRepository.findByEmail(email)).willReturn(Optional.of(testUser));

		// when
		User user = userService.registerIfNeed(email);

		// then
		assertThat(user).isEqualTo(testUser);
	}

	@Test
	void 소셜로그인_신규회원가입() {
		// given
		String email = "newuser@social.com";
		given(userRepository.findByEmail(email)).willReturn(Optional.empty());

		ArgumentCaptor<User> userCaptor = ArgumentCaptor.forClass(User.class);
		given(userRepository.save(any(User.class))).willAnswer(invocation -> invocation.getArgument(0));

		// when
		User user = userService.registerIfNeed(email);

		// then
		assertThat(user.getEmail()).isEqualTo(email);
		assertThat(user.getPassword()).isEqualTo("SOCIAL2025!");
		assertThat(user.getAddress()).isEqualTo("소셜 로그인 유저");
		assertThat(user.getRole()).isEqualTo(UserRole.USER);
		assertThat(user.isDeleted()).isFalse();
		assertThat(user.getNickname()).startsWith("구글유저_");
	}




}
