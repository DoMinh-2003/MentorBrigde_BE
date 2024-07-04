package com.BE.exception.exceptions;

public class InvalidToken extends RuntimeException{
    public InvalidToken(String message){
        super(message);
    }
}
