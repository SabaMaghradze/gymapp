package com.gymapp.exception;

import com.gymapp.exception.auth.AuthenticationException;
import com.gymapp.exception.auth.BadCredentialsException;
import com.gymapp.exception.auth.InvalidCredentialsException;
import com.gymapp.exception.auth.LockedException;
import com.gymapp.exception.resource.ResourceAlreadyExistsException;
import com.gymapp.exception.resource.ResourceNotFoundException;
import com.gymapp.exception.role.RoleAlreadyExistsException;
import com.gymapp.exception.role.RoleNotFoundException;
import com.gymapp.exception.user.UserAlreadyExistsException;
import com.gymapp.exception.user.UserInactiveException;
import com.gymapp.exception.user.UserNotFoundException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.server.ResponseStatusException;

import java.util.HashMap;
import java.util.Map;

@RestControllerAdvice
public class GlobalExceptionHandler {

    private ResponseEntity<ErrorResponse> buildResponse(HttpStatus status, String message, WebRequest webRequest) {
        ErrorResponse error = new ErrorResponse(status.value(), message, webRequest.getContextPath());
        return new ResponseEntity<>(error, status);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleAllExceptions(Exception ex, WebRequest req) {
        return buildResponse(HttpStatus.INTERNAL_SERVER_ERROR, ex.getMessage(), req);
    }

    // handles @Valid failures automatically.
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String, String>> handleValidationExceptions(MethodArgumentNotValidException exc) {
        Map<String, String> errors = new HashMap<>();
        exc.getBindingResult().getFieldErrors()
                .forEach(err -> errors.put(err.getField(), err.getDefaultMessage()));

        return ResponseEntity.badRequest().body(errors);
    }

    @ExceptionHandler({ResourceNotFoundException.class, UserNotFoundException.class, RoleNotFoundException.class})
    public ResponseEntity<ErrorResponse> handleNotFound(RuntimeException ex, WebRequest req) {
        return buildResponse(HttpStatus.NOT_FOUND, ex.getMessage(), req);
    }

    @ExceptionHandler({ResourceAlreadyExistsException.class, UserAlreadyExistsException.class, RoleAlreadyExistsException.class})
    public ResponseEntity<ErrorResponse> handleAlreadyExists(RuntimeException ex, WebRequest req) {
        return buildResponse(HttpStatus.CONFLICT, ex.getMessage(), req);
    }

    @ExceptionHandler({InvalidCredentialsException.class, BadCredentialsException.class})
    public ResponseEntity<ErrorResponse> handleBadCredentials(RuntimeException ex, WebRequest req) {
        return buildResponse(HttpStatus.UNAUTHORIZED, ex.getMessage(), req);
    }

    @ExceptionHandler(ResponseStatusException.class)
    public ResponseEntity<ErrorResponse> handleResponseStatus(ResponseStatusException ex, WebRequest req) {
        HttpStatus status = (HttpStatus) ex.getStatusCode();
        return buildResponse(status, ex.getReason(), req);
    }

    @ExceptionHandler(AuthenticationException.class)
    public ResponseEntity<ErrorResponse> handleAuth(AuthenticationException ex, WebRequest req) {
        return buildResponse(HttpStatus.UNAUTHORIZED, ex.getMessage(), req);
    }

    @ExceptionHandler({UserInactiveException.class, LockedException.class})
    public ResponseEntity<ErrorResponse> handleLocked(RuntimeException ex, WebRequest req) {
        return buildResponse(HttpStatus.LOCKED, ex.getMessage(), req);
    }

    @ExceptionHandler(WorkloadServiceUnavailableException.class)
    public ResponseEntity<ErrorResponse> handleWorkloadServiceUnavailable(WorkloadServiceUnavailableException ex, WebRequest req) {
        return buildResponse(HttpStatus.SERVICE_UNAVAILABLE, ex.getMessage(), req);
    }
}








