package com.example.rsupportnotice.config.exception;

public class NoticeNotFoundException extends RuntimeException {
    public NoticeNotFoundException(long id) {
        super("공지 사항이 존재하지 않습니다. id = " + "[" + id + "]");
    }
}
