package deb8085;

import java.lang.Throwable;

public class BreakPointListException extends Throwable {
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