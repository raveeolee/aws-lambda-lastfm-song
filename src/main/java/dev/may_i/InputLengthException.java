package dev.may_i;

import java.lang.RuntimeException;

public class InputLengthException extends RuntimeException { 
    public InputLengthException(String errorMessage) {
        super(errorMessage);
    }
}