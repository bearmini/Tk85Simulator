package com.bearmini.tk85.base8085;

public class PublicLabelListException extends Exception {
    /**
     *
     */
    private static final long serialVersionUID = -5280670388231321408L;

    public String message;

    public PublicLabelListException() {
    }

    public PublicLabelListException(String s) {
        message = s;
    }

}
