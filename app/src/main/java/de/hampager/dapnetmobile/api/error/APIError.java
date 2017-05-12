package de.hampager.dapnetmobile.api.error;

public class APIError {
    private int statusCode;
    private String name;
    private String message;

    public APIError() {

    }

    public int status() {
        return statusCode;
    }

    public String message() {
        return message;
    }

    @Override
    public String toString() {
        return "code: " + statusCode + " name: " + name + " message: " + message;
    }
}
