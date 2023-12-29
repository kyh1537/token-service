package com.example.tokenservice.constant;

import lombok.Getter;

@Getter
public enum Errors {

    GENERAL_UNKNOWN(-10, "에러", "UNKNOWN ERROR"),
    AUTH_TOKEN_NOT_FOUND_ERR(-403, "에러", "인증 정보를 확인해주세요."),
    VALIDATION_USER_ID_ERR(-1000, "에러", "이미 사용 중인 아이디 입니다."),
    VALIDATION_USER_EMAIL_ERR(-1001, "에러", "이미 사용 중인 이메일 입니다."),
    USER_NOT_FOUND_ERR(-1002, "에러", "유저 정보를 찾을 수 없습니다."),
    PASSWORD_NOT_MATCH_ERR(-1003, "에러", "비밀번호가 일치하지 않습니다."),
    AUTH_TOKEN_EXPIRE_ERR(-1005, "에러", "사용 할 수 없는 토큰 입니다."),
    ;


    private final int code;
    private final String title;
    private final String message;

    Errors(int code, String title, String message) {
        this.code = code;
        this.title = title;
        this.message = message;
    }
}
