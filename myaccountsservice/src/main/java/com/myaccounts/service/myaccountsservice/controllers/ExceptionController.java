package com.myaccounts.service.myaccountsservice.controllers;

import com.myaccounts.service.myaccountsservice.components.PropsSesionComponent;
import org.springframework.beans.factory.annotation.Autowired;
import com.myaccounts.service.myaccountsservice.exceptions.MyBadRequestException;
import com.myaccounts.service.myaccountsservice.exceptions.ReLodingException;
import com.myaccounts.service.myaccountsservice.exceptions.RefreshException;
import com.myaccounts.service.myaccountsservice.models.dtos.ErrorDto;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.http.ResponseCookie;

@RestControllerAdvice
public class ExceptionController {

    @Autowired
    private PropsSesionComponent component;

    @ExceptionHandler({
        ReLodingException.class,
        RefreshException.class
    })
    public ResponseEntity<?> unauthorized(Exception e) {
        ErrorDto errorDto = new ErrorDto(HttpStatus.UNAUTHORIZED, e.getMessage());

        if (e instanceof ReLodingException) {
            ResponseCookie cookie = ResponseCookie.from("the_cookie", "")
                    .sameSite(component.getSameStite())
                    .httpOnly(component.getHttpOnly())
                    .secure(component.getSecurity())
                    .maxAge(0)
                    .path(component.getPath()).build();
            HttpHeaders headers = new HttpHeaders();
            headers.add("Set-Cookie", cookie.toString());
            return ResponseEntity.status(errorDto.getStatusCode()).headers(headers).body(errorDto);
        }

        return ResponseEntity.status(errorDto.getStatusCode()).body(errorDto);
    }

    @ExceptionHandler({
        MyBadRequestException.class,
        MethodArgumentNotValidException.class
    })
    public ResponseEntity<?> badRequest(Exception e) {
        ErrorDto errorDto = new ErrorDto(HttpStatus.BAD_REQUEST, e.getMessage());

        if(e instanceof MethodArgumentNotValidException err){
            StringBuilder stringBuilder = new StringBuilder();
            for(FieldError field: err.getFieldErrors()){
                stringBuilder.append(field.getField()).append(": ")
                        .append(field.getDefaultMessage()).append(". ");
            }
            errorDto.setMessage(stringBuilder.toString().trim());
        }

        return ResponseEntity.status(errorDto.getStatusCode()).body(errorDto);
    }
}