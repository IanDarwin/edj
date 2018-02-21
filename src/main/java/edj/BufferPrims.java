package edj;

import java.io.IOException;
import java.util.List;

public interface BufferPrims {
	
	final int NO_NUM = 0, INF = Integer.MAX_VALUE;
	
	static int current = 0;

	void addLines(List<String> newLines);

	void addLines(int starting, List<String> newLines);

	void deleteLines(int start, int end);
	
	void clearBuffer();
	
	void readBuffer(String fileName) throws IOException;

	int getCurrentLineNumber();

	/** Print one or more of lines */
	void printLines(int i, int j);
	
	/** Undo the most recent operation */
	void undo();

}
