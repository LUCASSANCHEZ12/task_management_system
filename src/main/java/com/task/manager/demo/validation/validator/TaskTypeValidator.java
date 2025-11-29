package com.task.manager.demo.validation.validator;

import com.task.manager.demo.entity.Type_Enum;
import com.task.manager.demo.validation.annotation.ValidTaskType;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class TaskTypeValidator  implements ConstraintValidator<ValidTaskType, String> {
    @Override
    public boolean isValid(String s, ConstraintValidatorContext constraintValidatorContext) {
        return s == null || Type_Enum.isValid(s);
    }
}
