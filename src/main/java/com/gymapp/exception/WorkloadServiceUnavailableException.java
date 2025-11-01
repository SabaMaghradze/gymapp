package com.gymapp.exception;

public class WorkloadServiceUnavailableException extends RuntimeException {
    public WorkloadServiceUnavailableException(String message) {
        super(message);
    }
}
