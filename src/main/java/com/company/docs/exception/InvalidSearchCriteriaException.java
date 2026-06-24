package com.company.docs.exception;

import org.springframework.http.HttpStatus;

public class InvalidSearchCriteriaException extends ApiException {

    public InvalidSearchCriteriaException(String message) {
        super(HttpStatus.BAD_REQUEST, "INVALID_SEARCH_CRITERIA", message);
    }
}
