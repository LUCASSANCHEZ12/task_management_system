package com.task.manager.demo.user.controller.advice.exceptions;

public class NullIDException extends RuntimeException {
    public NullIDException(String message) {
        super(message);
    }
}
