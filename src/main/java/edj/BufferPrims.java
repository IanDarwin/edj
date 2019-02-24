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
	
	void addLine(String newLine);
	
	void addLines(List<String> newLines);

	void addLines(int start, List<String> newLines);

	void deleteLines(int start, int end);
	
	void clearBuffer();
	
	void readBuffer(String fileName);
	
	default void writeBuffer(String fileName) {
		throw new UnsupportedOperationException();
	}

	int getCurrentLineNumber();
	String getCurrentLine();
	int goToLine(int n);
	int size();			// Number of lines, as per old Collections

	/** Retrieve one or more of lines */
	String getLine(int ln);
	List<String> getLines(int i, int j);

	/** replace first/all occurrence of 'old' regex w 'new' text, current line */
	void replace(String oldRE, String newStr, boolean all);	
	/** replace first/all occur in each line */
	void replace(String oldRE, String newStr, boolean all, int startLine, int endLine); 

	boolean isUndoSupported();
	
	/** Undo the most recent operation: optional method */
	default void undo() {
		throw new UnsupportedOperationException();
	}

}
