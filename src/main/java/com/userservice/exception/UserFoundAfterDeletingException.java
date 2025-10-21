package com.userservice.exception;

public class UserFoundAfterDeletingException extends Exception{
    public UserFoundAfterDeletingException(String message){
        super(message);
    }
}
