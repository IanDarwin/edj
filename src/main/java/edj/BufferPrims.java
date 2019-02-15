package edj;

import java.util.List;

/**
 * Buffer Primitives for Line Editor edj.
 * 
 * This interface provides separation between the command level and the buffer management.
 * The internal organization of the buffer is not the concernt of this interface,
 * other than to the extent that it is a line-oriented interface.
 * 
 * N.B. ALL Line numbers at this interface's level are 1-based, because 1 is the first line.
 * "Line 0" means there are no lines in the buffer.
 *
 * @author Ian Darwin
 */
public interface BufferPrims {
	
	final int NO_NUM = 0,
		INF = Integer.MAX_VALUE;
	
	void addLines(List<String> newLines);

	void addLines(int start, List<String> newLines);

	void deleteLines(int start, int end);
	
	void clearBuffer();
	
	void readBuffer(String fileName);
	
	default void writeBuffer(String fileName) {
		throw new UnsupportedOperationException();
	}

	int getCurrentLineNumber();
	int goToLine(int n);
	int size();			// Number of lines, as per old Collections

	/** Print one or more of lines (presumably to stdout) */
	void printLines(int i, int j);

	// void replace(old, new);	// replace first occurrence, current line
	// void replaceAll(old, new); // replace All occurrences
	// void replace(old, new, startLine, endLine); // replace first occur in each line
	// void replaceAll(old, new, startLine, endLine);
	
	boolean isUndoSupported();
	
	/** Undo the most recent operation: optional method */
	default void undo() {
		throw new UnsupportedOperationException();
	}

}
