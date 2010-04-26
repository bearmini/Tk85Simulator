package deb8085;

import java.lang.Throwable;

public class BreakPointListException extends Throwable {
	String message;

	public BreakPointListException() {
	}

	public BreakPointListException(String s) {
		message = s;
	}

}