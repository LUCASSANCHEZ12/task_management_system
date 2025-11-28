package com.task.manager.demo.entity;

public enum Type_Enum {
    TASK("task"),
    SUBTASK("subtask");

    private String value;
    Type_Enum(String value) {
        this.value = value;
    }
}
