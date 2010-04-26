package deb8085;

public class OnEncodeException extends Throwable {
	public String message;

	public OnEncodeException() {
	}

	public OnEncodeException(String s) {
		message = s;
	}

}