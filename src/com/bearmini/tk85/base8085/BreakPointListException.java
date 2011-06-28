package com.bearmini.tk85.base8085;

/**
 */
public class BreakPointListException extends Exception {
    /**
     *
     */
    private static final long serialVersionUID = -7084125307012394855L;

    public String message;

    /**
     * Constructor.
     */
    public BreakPointListException() {
    }

    public BreakPointListException(final String s) {
        message = s;
    }

}
