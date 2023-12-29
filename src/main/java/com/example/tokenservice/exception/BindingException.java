package com.example.tokenservice.exception;

import org.springframework.validation.FieldError;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class BindingException extends RuntimeException {

	private final FieldError fieldError;

	public static BindingException of(FieldError fieldError) {
		return BindingException.builder().fieldError(fieldError).build();
	}
}
