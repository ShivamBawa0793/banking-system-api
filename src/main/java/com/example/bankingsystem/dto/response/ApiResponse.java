package com.example.bankingsystem.dto.response;

public class ApiResponse {
    private Boolean success;
    private String message;
    private String errorCode;

    public ApiResponse(String message, Boolean success) {
        this.message = message;
        this.success = success;
    }

    public ApiResponse(String errorCode, String message, Boolean success) {
        this.errorCode = errorCode;
        this.message = message;
        this.success = success;
    }

    public String getErrorCode() {
        return errorCode;
    }

    public void setErrorCode(String errorCode) {
        this.errorCode = errorCode;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public Boolean getSuccess() {
        return success;
    }

    public void setSuccess(Boolean success) {
        this.success = success;
    }
}
