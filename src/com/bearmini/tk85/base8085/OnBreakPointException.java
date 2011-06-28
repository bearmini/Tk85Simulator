package com.bearmini.tk85.base8085;

public class OnBreakPointException extends Exception {
    /**
     *
     */
    private static final long serialVersionUID = -200400590564122488L;

    public String message;

    public OnBreakPointException() {
    }

    public OnBreakPointException(String s) {
        message = s;
    }

}
