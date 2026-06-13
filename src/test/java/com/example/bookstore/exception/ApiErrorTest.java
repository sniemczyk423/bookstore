package com.example.bookstore.exception;

import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

class ApiErrorTest {

    @Test
    void shouldCreateApiErrorAndReturnItsValues() {
        Map<String, String> validationErrors = Map.of(
                "email",
                "email must be valid"
        );

        ApiError apiError = new ApiError(
                400,
                "Bad Request",
                "request validation failed",
                "/api/auth/register",
                validationErrors
        );

        assertEquals(400, apiError.getStatus());
        assertEquals("Bad Request", apiError.getError());
        assertEquals(
                "request validation failed",
                apiError.getMessage()
        );
        assertEquals(
                "/api/auth/register",
                apiError.getPath()
        );
        assertEquals(
                validationErrors,
                apiError.getValidationErrors()
        );
        assertNotNull(apiError.getTimestamp());
        assertTrue(
                apiError.getTimestamp()
                        .isBefore(LocalDateTime.now().plusSeconds(1))
        );
    }
}