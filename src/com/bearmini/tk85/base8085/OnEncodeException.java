package com.bearmini.tk85.base8085;

public class OnEncodeException extends Exception {
    /**
     */
    private static final long serialVersionUID = -7484187874803062386L;

    public String message;

    public OnEncodeException() {
    }

    public OnEncodeException(String s) {
        message = s;
    }

}
