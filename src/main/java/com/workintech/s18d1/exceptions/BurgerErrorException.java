package com.workintech.s18d1.exceptions;

import org.springframework.http.HttpStatus;

public class BurgerErrorException extends BurgerException {

    public BurgerErrorException(String message, HttpStatus httpStatus) {
        super(message, httpStatus);
    }
}
