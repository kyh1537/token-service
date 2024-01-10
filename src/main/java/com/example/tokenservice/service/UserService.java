package com.example.tokenservice.service;

import java.util.Optional;
import java.util.UUID;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.ObjectUtils;

import com.example.tokenservice.constant.Errors;
import com.example.tokenservice.controller.dto.TokenInfoDto;
import com.example.tokenservice.controller.dto.UserDto;
import com.example.tokenservice.controller.dto.UserDto.CreateUserRes;
import com.example.tokenservice.controller.dto.UserDto.LoginReq;
import com.example.tokenservice.controller.dto.UserDto.LoginRes;
import com.example.tokenservice.controller.dto.UserDto.UserCreateReq;
import com.example.tokenservice.exception.CommonException;
import com.example.tokenservice.model.RefreshTokens;
import com.example.tokenservice.model.User;
import com.example.tokenservice.persist.RefreshTokensPersist;
import com.example.tokenservice.persist.UserPersist;
import com.example.tokenservice.util.JwtTokenProvider;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class UserService {

	private final UserPersist userPersist;
	private final RefreshTokensPersist tokenPersist;
	private final PasswordEncoder passwordEncoder;
	private final JwtTokenProvider jwtTokenProvider;

	/**
	 * 유저 회원 가입 API 로직
	 */
	@Transactional
	public CreateUserRes createUser(UserCreateReq req) {

		this.validationPw(req.getPassword(), req.getCheckPassword());
		this.validationMail(req.getEmail());

		// 유저 정보 생성
		String uid = UUID.randomUUID().toString().toUpperCase();
		String pw = this.passwordEncoder.encode(req.getPassword());
		this.userPersist.save(User.of(uid, req.getEmail(), pw, req.getName(), req.getCellPhone()));

		return CreateUserRes.builder().id(uid).build();
	}

	// /**
	//  * 유저 정보 수정 API 로직
	//  */
	// @Transactional
	// public void updateUser(String id, UserCreateDto req) {
	//     User originalUser = this.userPersist.findById(id, false);
	//
	//     // 메일이 다르면 체크
	//     if (!req.getEmail().equals(originalUser.getEmail())) {
	//         this.validationMail(req.getEmail());
	//     }
	//
	//     // 유저 정보 수정
	//     originalUser.updateUserInfo(req.getEmail(), req.getName(), req.getCellPhone());
	//     this.userPersist.save(originalUser);
	// }

	// /**
	//  * 사용자 탈퇴 API 로직
	//  */
	// @Transactional
	// public void withdrawUser(String id) {
	//     User originalUser = this.userPersist.findById(id, false);
	//     originalUser.withdraw();
	//     this.userPersist.save(originalUser);
	// }

	/**
	 * Login API 로직(멀티 로그인 지원 X)
	 */
	@Transactional
	public LoginRes login(LoginReq req) {

		// 아이디 검사
		User user = this.userPersist.findByEmail(req.getEmail()).orElseThrow(
			() -> CommonException.of(Errors.USER_NOT_FOUND_ERR)
		);

		// 비밀번호 검증
		if (!passwordEncoder.matches(req.getPassword(), user.getPassword())) {
			throw CommonException.of(Errors.PASSWORD_NOT_MATCH_ERR);
		}

		// 기존 토큰 정보 모두 만료 처리
		this.tokenPersist.expireToken(user.getId(), "신규 로그인");

		// Token 생성 및 refreshToken DB 저장
		TokenInfoDto tokenDto = this.jwtTokenProvider.createAllToken(user);
		RefreshTokens refreshToken = RefreshTokens.of(tokenDto.getRefreshToken(), user.getId());
		this.tokenPersist.save(refreshToken);

		return LoginRes.builder().token(tokenDto).build();
	}

	/**
	 * 로그아웃 API
	 */
	@Transactional
	public void logout(User user) {
		this.tokenPersist.expireToken(user.getId(), "로그아웃");
	}

	/**
	 * refresh API 로직
	 */
	public LoginRes refreshToken(UserDto.RefreshReq req) {

		RefreshTokens tokenInfo = this.tokenPersist.findByRefreshToken(req.getToken());
		User user = this.userPersist.findById(tokenInfo.getUid());

		return LoginRes.builder()
			.token(this.jwtTokenProvider.createRefreshToken(user, req.getToken()))
			.build();
	}

	/**
	 * 이메일 중복 체크
	 */
	private void validationMail(String email) {
		Optional<User> checkUser = this.userPersist.findByEmail(email);
		if (checkUser.isPresent()) {
			throw CommonException.of(Errors.VALIDATION_USER_EMAIL_ERR);
		}
	}

	/**
	 * 패스워드 확인 체크
	 */
	private void validationPw(String pw1, String pw2) {
		if (ObjectUtils.isEmpty(pw1) || ObjectUtils.isEmpty(pw2) || !pw1.equals(pw2)) {
			throw CommonException.of(Errors.PASSWORD_NOT_MATCH_ERR);
		}
	}
}
