package com.example.tokenservice.controller;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.tokenservice.controller.dto.UserDto;
import com.example.tokenservice.controller.dto.UserDto.LoginRes;
import com.example.tokenservice.controller.dto.UserDto.CreateUserRes;
import com.example.tokenservice.controller.dto.UserDto.UserInfoRes;
import com.example.tokenservice.exception.BindingException;
import com.example.tokenservice.model.User;
import com.example.tokenservice.service.UserService;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/user")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @PostMapping("/v1")
    public ResponseEntity<CreateUserRes> createUser(
            @Valid @RequestBody UserDto.UserCreateDto req,
            BindingResult result) {

        if (result.hasErrors()) {
            throw new BindingException(result.getFieldError());
        }

        return new ResponseEntity<>(this.userService.createUser(req), HttpStatus.CREATED);
    }

    @PostMapping("/v1/login")
    public ResponseEntity<LoginRes> login(
            @Valid @RequestBody UserDto.LoginDto req,
            BindingResult result) {

        if (result.hasErrors()) {
            throw new BindingException(result.getFieldError());
        }

        return new ResponseEntity<>(this.userService.login(req), HttpStatus.CREATED);
    }

    /**
     * 토큰에 저장된 본인 정보를 가져온다.
     */
    @GetMapping("/v1/me")
    public ResponseEntity<UserInfoRes> getUserInfo(HttpServletRequest request) {
        User user = (User) request.getAttribute("tokenInfo");
        return new ResponseEntity<>(UserInfoRes.of(user), HttpStatus.OK);
    }
}
