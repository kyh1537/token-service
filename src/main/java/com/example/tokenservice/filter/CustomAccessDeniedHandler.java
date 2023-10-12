package com.example.tokenservice.filter;

import java.io.IOException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.stereotype.Component;

import com.example.tokenservice.constant.Errors;
import com.example.tokenservice.controller.exception.ErrorResponse;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Component
public class CustomAccessDeniedHandler implements AccessDeniedHandler {

    private final ObjectMapper objectMapper;

    @Override
    public void handle(HttpServletRequest req, HttpServletResponse res, AccessDeniedException accessDeniedException)
            throws IOException {

        Errors errors = Errors.AUTH_TOKEN_NOT_FOUND_ERR;
        ErrorResponse errorResponse = new ErrorResponse(errors.getCode(), errors.getTitle(), errors.getMessage());

        res.setCharacterEncoding("UTF-8");
        res.setStatus(HttpStatus.FORBIDDEN.value());
        res.setContentType(MediaType.APPLICATION_JSON_VALUE);
        res.getWriter().write(this.objectMapper.writeValueAsString(errorResponse));
    }
}