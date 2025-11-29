package com.task.manager.demo.entity;

public enum Type_Enum {
    TASK("task"),
    SUBTASK("subtask");

    private String value;
    Type_Enum(String value) {
        this.value = value;
    }

    public static boolean isValid(String value) {
        for (Type_Enum tz : values()) {
            if (tz.value.equals(value.toLowerCase())) return true;
        }
        return false;
    }
}
