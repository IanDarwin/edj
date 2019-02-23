package edj;

/** 
 * Represents the command information from one command.
 * What is to be done, but not how.
 */
public class ParsedLine {
	// Could be an enum but we use it to index an array directly
	char cmdLetter; // 'a' for append, 'd' for delete, &c.
	boolean startFound, commaFound, endFound;
	int startNum, endNum;
	String operands; // The rest of the line
	Object opaque;	// For use of command

	public String toString() {
		return String.format("%d,%d%c%s", startNum, endNum, cmdLetter, 
			operands == null ? "" :  (' ' + operands));
	}
	/** Convert line range into actual numbers:
	 * n,m means n through m inclusive
	 * n, means n through end (INF)
	 * ,m means 1 through m
	 * , means 1 through end
	 * @return The start and end, as array of integers.
	 */
	public int[] lineRange() {
		if (startFound && commaFound && endFound) {
			return new int[] {startNum, endNum};
		}
		if (startFound && commaFound && !endFound) {
			return new int[] {startNum, BufferPrims.INF};
		}
		if (!startFound && commaFound && endFound) {
			return new int[] {1, endNum};
		}
		if (commaFound) {
			return new int[] {1, BufferPrims.INF };
		}
		return new int[] {};
	}
}

