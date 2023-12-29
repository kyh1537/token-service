package com.example.tokenservice.service;

import java.time.LocalDateTime;
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
import com.example.tokenservice.controller.dto.UserDto.UserCreateDto;
import com.example.tokenservice.exception.CommonException;
import com.example.tokenservice.model.AccessTokens;
import com.example.tokenservice.model.User;
import com.example.tokenservice.model.User.Status;
import com.example.tokenservice.persist.AccessTokensPersist;
import com.example.tokenservice.persist.UserPersist;
import com.example.tokenservice.util.JwtTokenProvider;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class UserService {

	private final UserPersist userPersist;
	private final AccessTokensPersist tokenPersist;
	private final PasswordEncoder passwordEncoder;
	private final JwtTokenProvider jwtTokenProvider;

	/**
	 * 유저 회원 가입 API 로직
	 */
	@Transactional
	public CreateUserRes createUser(UserCreateDto req) {

		this.validationPw(req.getPassword(), req.getCheckPassword());
		this.validationMail(req.getEmail());

		// 유저 정보 생성
		String uid = UUID.randomUUID().toString().toUpperCase();
		User user = User.builder()
			.id(uid)
			.email(req.getEmail())
			.password(this.passwordEncoder.encode(req.getPassword()))
			.name(req.getName())
			.cellphone(req.getCellPhone())
			.status(Status.ACTIVE)
			.createUser(uid)
			.updateUser(uid)
			.build();

		// 유저 정보 저장
		this.userPersist.save(user);
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

	@Transactional
	public LoginRes login(LoginReq req) {

		// 아이디 검사
		User user = this.userPersist.findByEmail(req.getEmail()).orElseThrow(
			() -> new CommonException(Errors.USER_NOT_FOUND_ERR)
		);

		// 비밀번호 검증
		if (!passwordEncoder.matches(req.getPassword(), user.getPassword())) {
			throw new CommonException(Errors.PASSWORD_NOT_MATCH_ERR);
		}

		// 기존 토큰 정보 모두 만료 처리
		this.tokenPersist.expireToken(user.getId());

		// Token 생성 및 DB 저장
		TokenInfoDto tokenDto = this.jwtTokenProvider.createAllToken(user.getId());
		AccessTokens newToken = AccessTokens.of(tokenDto.getRefreshToken(), user.getId());
		this.tokenPersist.save(newToken);

		return LoginRes.builder().token(tokenDto).build();
	}

	/**
	 * refresh API 로직
	 */
	public LoginRes refreshToken(UserDto.RefreshReq req) {

		AccessTokens tokenInfo = this.tokenPersist.findByRefreshToken(req.getToken());
		if (ObjectUtils.isEmpty(tokenInfo)) {
			throw new CommonException(Errors.AUTH_TOKEN_EXPIRE_ERR);
		}

		return LoginRes.builder()
			.token(this.jwtTokenProvider.createRefreshToken(tokenInfo.getUid(), req.getToken()))
			.build();
	}

	/**
	 * 이메일 중복 체크
	 */
	private void validationMail(String email) {
		Optional<User> checkUser = this.userPersist.findByEmail(email);
		if (checkUser.isPresent()) {
			throw new CommonException(Errors.VALIDATION_USER_EMAIL_ERR);
		}
	}

	/**
	 * 패스워드 확인 체크
	 */
	private void validationPw(String pw1, String pw2) {
		if (ObjectUtils.isEmpty(pw1) || ObjectUtils.isEmpty(pw2) || !pw1.equals(pw2)) {
			throw new CommonException(Errors.PASSWORD_NOT_MATCH_ERR);
		}
	}
}
