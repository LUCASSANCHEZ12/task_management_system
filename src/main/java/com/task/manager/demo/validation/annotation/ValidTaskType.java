package com.task.manager.demo.validation.annotation;

import com.task.manager.demo.validation.validator.TaskTypeValidator;
import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target({ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = TaskTypeValidator.class)
public @interface ValidTaskType {

    String message() default "Invalid task type. The only types accepted are: [TASK, SUBTASK]";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}
