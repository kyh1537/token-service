package com.example.tokenservice.exception;

import javax.servlet.http.HttpServletRequest;
import org.springframework.boot.web.servlet.error.ErrorController;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.NoHandlerFoundException;

import com.example.tokenservice.constant.Errors;

import lombok.extern.slf4j.Slf4j;

@ControllerAdvice
@RestController
@Slf4j
public class ExceptionAdvice implements ErrorController {

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> exception(HttpServletRequest req, Exception e) {

        ErrorResponse response = new ErrorResponse();
        response.setStatus(Errors.GENERAL_UNKNOWN.getCode());
        response.setTitle(Errors.GENERAL_UNKNOWN.getTitle());
        response.setMessage(Errors.GENERAL_UNKNOWN.getMessage());

        req.setAttribute("error", e);
        log.error("[************************ printStackTrace ************************]", e);
        return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @ExceptionHandler(NoHandlerFoundException.class)
    public ResponseEntity<ErrorResponse> handleNoHandlerFound(HttpServletRequest req, NoHandlerFoundException e) {

        ErrorResponse response = new ErrorResponse();
        response.setStatus(Errors.GENERAL_UNKNOWN.getCode());
        response.setTitle(Errors.GENERAL_UNKNOWN.getTitle());
        response.setMessage(Errors.GENERAL_UNKNOWN.getMessage());

        req.setAttribute("error", response);

        return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @ExceptionHandler(BindException.class)
    public ResponseEntity<ErrorResponse> bind(HttpServletRequest req, BindException e) {

        ErrorResponse response = new ErrorResponse();
        response.setStatus(HttpStatus.INTERNAL_SERVER_ERROR.value());
        response.setTitle("ERROR");
        response.setMessage("형식에 맞지 않거나, 필수 값이 없습니다.");

        req.setAttribute("warn", e);
        req.setAttribute("warn-response", response);

        return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @ExceptionHandler(BindingException.class)
    public ResponseEntity<ErrorResponse> bindingException(HttpServletRequest req, BindingException e) {

        ErrorResponse response = new ErrorResponse();
        response.setStatus(HttpStatus.BAD_REQUEST.value());
        response.setTitle(HttpStatus.BAD_REQUEST.getReasonPhrase());
        response.setMessage(e.getFieldError().getField() + " : " + e.getFieldError().getDefaultMessage());

        req.setAttribute("warn", e);
        req.setAttribute("warn-response", response);

        return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(CommonException.class)
    public ResponseEntity<ErrorResponse> companyException(HttpServletRequest request, CommonException e) {

        ErrorResponse response = new ErrorResponse();
        response.setStatus(e.getError().getCode());
        response.setTitle(e.getError().getTitle());
        response.setMessage(e.getMessage());

        request.setAttribute("warn", e);
        request.setAttribute("warn-response", response);

        return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
    }
}
