package com.example.bookstore.exception;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.LinkedHashMap;
import java.util.Map;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler({
            BookNotFoundException.class,
            ReservationNotFoundException.class,
            RoleNotFoundException.class
    })
    public ResponseEntity<ApiError> handleNotFound(
            RuntimeException exception,
            HttpServletRequest request
    ) {
        return createResponse(
                HttpStatus.NOT_FOUND,
                exception.getMessage(),
                request,
                null
        );
    }

    @ExceptionHandler({
            IsbnAlreadyExistsException.class,
            EmailAlreadyExistsException.class,
            BookUnavailableException.class,
            InvalidReservationOperationException.class
    })
    public ResponseEntity<ApiError> handleBadRequest(
            RuntimeException exception,
            HttpServletRequest request
    ) {
        return createResponse(
                HttpStatus.BAD_REQUEST,
                exception.getMessage(),
                request,
                null
        );
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiError> handleValidation(
            MethodArgumentNotValidException exception,
            HttpServletRequest request
    ) {
        Map<String, String> validationErrors = new LinkedHashMap<>();

        exception.getBindingResult()
                .getFieldErrors()
                .forEach(error ->
                        validationErrors.put(
                                error.getField(),
                                error.getDefaultMessage()
                        )
                );

        return createResponse(
                HttpStatus.BAD_REQUEST,
                "request validation failed",
                request,
                validationErrors
        );
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiError> handleUnexpectedException(
            Exception exception,
            HttpServletRequest request
    ) {
        return createResponse(
                HttpStatus.INTERNAL_SERVER_ERROR,
                "unexpected application error",
                request,
                null
        );
    }

    private ResponseEntity<ApiError> createResponse(
            HttpStatus status,
            String message,
            HttpServletRequest request,
            Map<String, String> validationErrors
    ) {
        ApiError error = new ApiError(
                status.value(),
                status.getReasonPhrase(),
                message,
                request.getRequestURI(),
                validationErrors
        );

        return ResponseEntity.status(status).body(error);
    }
}