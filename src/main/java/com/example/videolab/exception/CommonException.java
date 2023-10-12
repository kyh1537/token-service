package com.example.videolab.exception;

import com.example.videolab.constant.Errors;
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
