package deb8085;

public class BreakPointListException extends Exception {
    /**
	 * 
	 */
    private static final long serialVersionUID = -7084125307012394855L;

    String message;

    public BreakPointListException() {
    }

    public BreakPointListException(String s) {
        message = s;
    }

}