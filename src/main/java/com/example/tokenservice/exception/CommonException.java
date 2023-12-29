package com.example.tokenservice.exception;

import com.example.tokenservice.constant.Errors;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@AllArgsConstructor
@Builder
public class CommonException extends RuntimeException {

    private final Errors error;
    private final String message;

    public static CommonException of(Errors e) {
        return CommonException.builder()
            .error(e)
            .message(e.getMessage())
            .build();
    }
}
