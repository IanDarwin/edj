package edj;

public class BufferUtils {

	// Utility methods, only for implementations that use a 0-based
	// internal representation (interface values are 1-based).
	public static int lineNumToIndex(int ln) {
		if (ln == 0) {
			ln = 1;
		}
		return ln - 1;
	}
	public static int indexToLineNum(int ix) {
		return ix + 1;
	}
}
