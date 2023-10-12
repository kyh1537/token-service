package com.example.tokenservice.controller.dto;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;

import lombok.Builder;
import lombok.Data;
import lombok.Getter;

public class UserDto {

    @Data
    public static class UserCreateDto {
        @NotBlank(message = "이메일을 입력해 주세요.")
        @Email
        private String email;

        @NotBlank(message = "비밀번호를 입력해 주세요.")
        private String password;

        @NotBlank(message = "비밀번호 확인을 입력해 주세요.")
        private String checkPassword;

        @NotBlank(message = "이름을 입력해 주세요.")
        private String name;

        @Pattern(regexp = "^01[0-9]{8,9}$", message = "전화번호 형식에 맞지 않습니다.")
        private String cellPhone;
    }

    @Data
    public static class LoginDto {
        @NotBlank(message = "이메일을 입력해 주세요.")
        private String email;

        @NotBlank(message = "비밀번호를 입력해 주세요.")
        private String password;
    }

    @Builder
    @Getter
    public static class UserInfoRes {
        private String id;
    }

    @Builder
    @Getter
    public static class LoginRes {
        private TokenInfoDto token;
    }
}
