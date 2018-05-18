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
	public String toString() {
		return String.format("%d,%d%c%s", startNum, endNum, cmdLetter, 
			operands == null ? "" :  (' ' + operands));
	}
}

