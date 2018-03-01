package edj;

public class ParsedLine {
	char cmdLetter; // 'a' for append, 'd' for delete, &c.
	boolean startFound, commaFound, endFound;
	int startNum, endNum;
	String operands; // The rest of the line
	public String toString() {
		return String.format("%d,%d%c%s", startNum, endNum, cmdLetter, 
			operands == null ? "" : operands);
	}
}