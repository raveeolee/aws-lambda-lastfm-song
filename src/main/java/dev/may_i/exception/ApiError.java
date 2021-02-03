package dev.may_i.exception;

public class ApiError {

    private String error;

    public ApiError(String error) {
        this.error = error;
    }

    public String getError() {
        return error;
    }
}
