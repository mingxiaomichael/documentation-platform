package com.company.docs.exception;

import org.springframework.http.HttpStatus;

public class InvalidPaginationException extends ApiException {

    public InvalidPaginationException(String message) {
        super(HttpStatus.BAD_REQUEST, "INVALID_PAGINATION", message);
    }
}
