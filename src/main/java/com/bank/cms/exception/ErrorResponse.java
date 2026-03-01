package com.bank.cms.exception;

public class ErrorResponse {

    private String message;
    private int status;
    private String timestamp;

    public ErrorResponse(String message, int status) {
        this.message = message;
        this.status = status;
        this.timestamp = java.time.LocalDateTime.now().toString();
    }

    public String getMessage() {
        return message;
    }

    public int getStatus() {
        return status;
    }

    public String getTimestamp() {
        return timestamp;
    }
}
