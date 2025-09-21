package com.gymapp.exception;

public record ErrorResponse(int status, String message, String path) {
}
