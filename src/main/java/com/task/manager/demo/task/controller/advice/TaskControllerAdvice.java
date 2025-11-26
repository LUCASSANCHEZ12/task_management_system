package com.task.manager.demo.task.controller.advice;

import com.task.manager.demo.task.controller.advice.exceptions.NullIDException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
public class TaskControllerAdvice {

    @ExceptionHandler(NullIDException.class)
    public ResponseEntity<String> manageNullIDException(NullIDException ex) {
        return new ResponseEntity<String>(ex.getMessage(), HttpStatus.BAD_REQUEST);
    }

}