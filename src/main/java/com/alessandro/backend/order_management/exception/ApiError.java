package com.alessandro.backend.order_management.exception;

import java.util.List;
import java.util.Map;

public class ApiError {

    private final int status;
    private final String error;
    private  final String message;
    private final String path;

    private final Map<String, List<String>> validationErrors;

    public ApiError(int status, String error, String message, String path) {
        this(status, error, message, path, null);
    }

    public ApiError(int status, String error, String message, String path, Map<String, List<String>> validationErrors) {
        this.status = status;
        this.error = error;
        this.message = message;
        this.path = path;
        this.validationErrors = validationErrors;
    }

    public int getStatus() { return status; }

    public String getError() {
        return error;
    }

    public String getMessage() {
        return message;
    }

    public String getPath() {
        return path;
    }

    public Map<String, List<String>> getValidationErrors() { return validationErrors; }
}
