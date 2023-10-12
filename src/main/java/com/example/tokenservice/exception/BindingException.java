package com.example.tokenservice.exception;

import org.springframework.validation.FieldError;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class BindingException extends RuntimeException {

    private final FieldError fieldError;
}