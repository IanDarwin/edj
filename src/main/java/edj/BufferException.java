package edj;

public class BufferException extends RuntimeException {
	private static final long serialVersionUID = 1L;

	BufferException(String mesg) {
		super(mesg);
	}
	BufferException(String mesg, Throwable t) {
		super(mesg, t);
	}
}
