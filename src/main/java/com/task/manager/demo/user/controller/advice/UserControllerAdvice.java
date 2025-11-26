package com.task.manager.demo.user.controller.advice;


import com.task.manager.demo.user.controller.advice.exceptions.NullIDException;
import com.task.manager.demo.user.controller.advice.exceptions.UserException;
import com.task.manager.demo.user.controller.advice.exceptions.UserNotFoundException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
public class UserControllerAdvice {

    @ExceptionHandler(NullIDException.class)
    public ResponseEntity<String> manageNullIDException(NullIDException ex) {
        return new ResponseEntity<String>(ex.getMessage(), HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(UserException.class)
    public ResponseEntity<String> manageUserException(UserException ex) {
        return new ResponseEntity<String>(ex.getMessage(), HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(UserNotFoundException.class)
    public ResponseEntity<String> manageUserNotFoundException(UserNotFoundException ex) {
        return new ResponseEntity<String>(ex.getMessage(), HttpStatus.NOT_FOUND);
    }

}