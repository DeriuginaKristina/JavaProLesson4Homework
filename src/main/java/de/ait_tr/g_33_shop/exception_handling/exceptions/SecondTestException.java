package de.ait_tr.g_33_shop.exception_handling.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.NOT_FOUND)
public class SecondTestException extends RuntimeException{
    public SecondTestException (String message){
        super(message);
    }
}
