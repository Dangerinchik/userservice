package com.userservice.exception;

public class CardInfoFoundAfterDeletingException extends Exception{
    public CardInfoFoundAfterDeletingException(String message){
        super(message);
    }
}
