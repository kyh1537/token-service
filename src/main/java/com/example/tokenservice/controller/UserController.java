package com.example.tokenservice.controller;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;

import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.tokenservice.controller.dto.UserDto;
import com.example.tokenservice.controller.dto.UserDto.CreateUserRes;
import com.example.tokenservice.controller.dto.UserDto.LoginRes;
import com.example.tokenservice.controller.dto.UserDto.UserInfoRes;
import com.example.tokenservice.exception.BindingException;
import com.example.tokenservice.model.User;
import com.example.tokenservice.service.UserService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/user")
@RequiredArgsConstructor
public class UserController extends CommonController {

	private final UserService userService;

	@PostMapping("/v1")
	public ResponseEntity<Void> createUser(
		@Valid @RequestBody UserDto.UserCreateReq req, BindingResult result) {

		if (result.hasErrors()) {
			throw BindingException.of(result.getFieldError());
		}

		this.userService.createUser(req);
		return ResponseEntity.ok().build();
	}

	@PostMapping("/v1/login")
	public ResponseEntity<LoginRes> login(@Valid @RequestBody UserDto.LoginReq req, BindingResult result) {

		if (result.hasErrors()) {
			throw BindingException.of(result.getFieldError());
		}

		return ResponseEntity.ok(this.userService.login(req));
	}

	@PostMapping("/v1/logout")
	public ResponseEntity<Void> logout() {
		User user = this.getUser();
		this.userService.logout(user);
		return ResponseEntity.ok().build();
	}

	@PostMapping("/v1/refresh")
	public ResponseEntity<LoginRes> refreshToken(@Valid @RequestBody UserDto.RefreshReq req, BindingResult result) {

		if (result.hasErrors()) {
			throw BindingException.of(result.getFieldError());
		}

		return ResponseEntity.ok(this.userService.refreshToken(req));
	}


	/**
	 * 토큰에 저장된 본인 정보를 가져온다.
	 */
	@GetMapping("/v1/me")
	public ResponseEntity<UserInfoRes> getUserInfo() {
		User user = this.getUser();
		return ResponseEntity.ok(UserInfoRes.of(user));
	}

	/**
	 * 사용자 탈퇴 처리
	 */
	@DeleteMapping("/v1")
	public ResponseEntity<Void> withdrawUser() {
		User user = this.getUser();
		this.userService.withdrawUser(user);
		return ResponseEntity.ok().build();
	}
}
