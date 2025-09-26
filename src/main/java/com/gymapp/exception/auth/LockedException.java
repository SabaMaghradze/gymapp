package com.gymapp.exception.auth;

public class LockedException extends RuntimeException {
    public LockedException(String message) {
        super(message);
    }
}
