package com.example.bookstore.exception;

public class RoleNotFoundException extends RuntimeException {

    public RoleNotFoundException(String roleName) {
        super("role " + roleName + " was not found");
    }
}