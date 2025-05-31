package com.example.rsupportnotice.config.exception;

public class AlreadyExistPhoneNumberException extends RuntimeException {
    public AlreadyExistPhoneNumberException(String phoneNumber) {
        super("이미 존재하는 학생입니다. " + "[" + phoneNumber + "]");
    }
}
