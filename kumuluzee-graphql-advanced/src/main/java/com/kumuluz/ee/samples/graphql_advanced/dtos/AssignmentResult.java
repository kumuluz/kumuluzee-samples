package com.kumuluz.ee.samples.graphql_advanced.dtos;

public class AssignmentResult {

    private String message;
    private Integer code;

    public AssignmentResult(String message, Integer code) {
        this.message = message;
        this.code = code;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public Integer getCode() {
        return code;
    }

    public void setCode(Integer code) {
        this.code = code;
    }
}
