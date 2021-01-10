package dev.may_i;

public class ApiError {

    private String error;

    public ApiError(String error) {
        this.error = error;
    }

    public String getError() {
        return error;
    }
}
