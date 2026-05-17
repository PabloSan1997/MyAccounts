package com.myaccounts.service.myaccountsservice.exceptions;

public class MyBadRequestException extends RuntimeException{
    public MyBadRequestException(String message){
        super(message);
    }
    public MyBadRequestException(){}
}