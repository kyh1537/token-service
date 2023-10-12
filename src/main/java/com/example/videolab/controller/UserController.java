package com.example.videolab.controller;

import javax.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.videolab.controller.dto.UserDto;
import com.example.videolab.controller.dto.UserDto.LoginRes;
import com.example.videolab.controller.dto.UserDto.UserInfoRes;
import com.example.videolab.exception.BindingException;
import com.example.videolab.service.UserService;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/user")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @PostMapping("/v1")
    public ResponseEntity<UserInfoRes> createUser(
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

    @PutMapping("/v1/{id}")
    public ResponseEntity<Void> updateUser(
            @PathVariable String id,
            @Valid @RequestBody UserDto.UserCreateDto req,
            BindingResult result) {

        if (result.hasErrors()) {
            throw new BindingException(result.getFieldError());
        }

        this.userService.updateUser(id, req);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @DeleteMapping("/v1/{id}")
    public ResponseEntity<Void> withdrawUser(
            @PathVariable String id) {

        this.userService.withdrawUser(id);
        return new ResponseEntity<>(HttpStatus.OK);
    }
}
