package com.BE.exception.exceptions;

public class NotAllowException extends RuntimeException{
    public NotAllowException(String mesage){
        super(mesage);
    }
}