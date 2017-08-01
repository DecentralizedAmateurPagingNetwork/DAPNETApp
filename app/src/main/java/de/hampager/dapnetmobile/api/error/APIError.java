package de.hampager.dapnetmobile.api.error;

public class APIError {
    private int statusCode;
    private String name;
    private String message;
    APIError(){
        this.statusCode=1000;
        this.name = "Unknown Error";
        this.message="There may have been an error while showing a different error";
    }
    public APIError(int statusCode, String name, String message) {
        this.statusCode = statusCode;
        this.name = name;
        this.message = message;
    }


    public String message() {
        return message;
    }

    @Override
    public String toString() {
        return "code: " + statusCode + " name: " + name + " message: " + message;
    }
}
