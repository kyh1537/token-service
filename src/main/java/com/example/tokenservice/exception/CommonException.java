package com.example.tokenservice.exception;

import com.example.tokenservice.constant.Errors;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class CommonException extends RuntimeException {

    private final Errors error;
    private final String message;

    public CommonException(Errors e) {
        this.error = e;
        this.message = e.getMessage();
    }
}
