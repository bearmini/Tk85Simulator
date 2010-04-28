package deb8085;

public class OnEncodeException extends Throwable {
	/**
	 * 
	 */
	private static final long serialVersionUID = -7484187874803062386L;
	
	public String message;

	public OnEncodeException() {
	}

	public OnEncodeException(String s) {
		message = s;
	}

}