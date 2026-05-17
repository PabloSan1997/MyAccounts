package com.myaccounts.service.myaccountsservice.models.dtos;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.http.HttpStatus;

import java.util.Date;

@Data
@NoArgsConstructor
public class ErrorDto {
    private String message;
    private Integer statusCode;
    private String error;
    private Date timestamp;

    public ErrorDto(HttpStatus status, String message){
        this.message = message;
        this.statusCode = status.value();
        this.error = status.getReasonPhrase();
        this.timestamp = new Date();
    }

    public static ErrorDto build(HttpStatus status, String message) {
        return new ErrorDto(status, message);
    }
}