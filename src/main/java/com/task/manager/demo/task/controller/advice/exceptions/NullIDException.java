package com.task.manager.demo.task.controller.advice.exceptions;

public class NullIDException extends RuntimeException {
    public NullIDException(String message) {
        super(message);
    }
}
