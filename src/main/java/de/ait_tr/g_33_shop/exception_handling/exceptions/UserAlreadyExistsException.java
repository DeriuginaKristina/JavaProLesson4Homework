package de.ait_tr.g_33_shop.exception_handling.exceptions;
public class UserAlreadyExistsException extends RuntimeException{
    public UserAlreadyExistsException(String message) {
        super(message);
    }
}

