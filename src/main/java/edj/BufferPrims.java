package edj;

import java.io.IOException;
import java.util.List;

/**
 * Buffer Primitives for Line Editor edj.
 * 
 * ALL Line numbers at this interface's level are 1-based, because 1 is the first line.
 * 
 * The editor doesn't really need this to be an interface, but want to have two 
 * implementations, with and without undo, for didactic purposes.
 *
 * @author Ian Darwin
 */
public interface BufferPrims {
	
	final int NO_NUM = 0,
		INF = Integer.MAX_VALUE;
	
	static int current = 0;

	void addLines(List<String> newLines);

	void addLines(int start, List<String> newLines);

	void deleteLines(int start, int end);
	
	void clearBuffer();
	
	void readBuffer(String fileName) throws IOException;

	int getCurrentLineNumber();
	
	int goToLine(int n);
	int size();			// as per old Collections

	/** Print one or more of lines */
	void printLines(int i, int j);
	
	/** Undo the most recent operation */
	void undo();

}
