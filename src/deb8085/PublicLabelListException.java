package deb8085;

import java.lang.Throwable;

public class PublicLabelListException extends Throwable {
	/**
	 * 
	 */
	private static final long serialVersionUID = -5280670388231321408L;
	
	String message;

	public PublicLabelListException() {
	}

	public PublicLabelListException(String s) {
		message = s;
	}

}