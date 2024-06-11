package com.example.tokenservice.controller;

import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;

import com.example.tokenservice.constant.Errors;
import com.example.tokenservice.dto.UserDetailsImpl;
import com.example.tokenservice.exception.CommonException;
import com.example.tokenservice.model.User;

@Controller
public class CommonController {

	public User getUser() {
		UserDetailsImpl userDetail = (UserDetailsImpl)SecurityContextHolder.getContext()
			.getAuthentication()
			.getPrincipal();

		User user = userDetail.getUser();
		if (User.Status.WITHDRAW.equals(user.getStatus())) {
			throw CommonException.of(Errors.WITHDRAW_USER_ERR);
		}

		return user;
	}
}
