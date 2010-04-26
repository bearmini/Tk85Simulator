package deb8085;

import java.lang.Throwable;

public class PublicLabelListException extends Throwable {
	String message;

	public PublicLabelListException() {
	}

	public PublicLabelListException(String s) {
		message = s;
	}

}