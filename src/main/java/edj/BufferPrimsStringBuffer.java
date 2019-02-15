package edj;

import java.util.Collections;
import java.util.List;

/**
 * Implement BufferPrims with a single long string with \n delimiting lines
 * @author Ian Darwin
 */
public class BufferPrimsStringBuffer implements BufferPrims {
	
	private StringBuffer buffer = new StringBuffer();
	int current;

	protected String getBuffer() {
		return buffer.toString();
	}

	protected void setBuffer(String contents) {
		current = 0;
		this.buffer = new StringBuffer(contents);
		if (buffer.charAt(buffer.length() - 1) != '\n')
			buffer.append('\n');
	}

	@Override
	public void addLines(List<String> newLines) {
		int n = size();
		for (String s : newLines) {
			buffer.append(s).append('\n');
		}
		current = n + newLines.size();
	}

	@Override
	public void addLines(int start, List<String> newLines) {
		int offset = findLineOffset(start);
		addLinesInternal(offset, newLines);
	}
	
	public void addLinesInternal(int offset, List<String> newLines) {
		for (String s : newLines) {
			buffer.insert(offset, s);
			offset += s.length();
			buffer.insert(offset, '\n');
			offset++;
		}
	}

	protected int findLineOffset(int start) {
		if (buffer == null) {
			throw new NullPointerException("findLine called with no buffer");
		}
		int j = 0, max = buffer.length();
		for (int i = 0; i < start; i++) {
			for (j = start; j < max; j++)
				if (buffer.charAt(j) == '\n') {
					++j;	// skip the newline
					break;
				}
		}
		return j > max ? -1 : j;
	}

	@Override
	public void deleteLines(int start, int end) {
		// TODO Auto-generated method stub

	}

	@Override
	public void clearBuffer() {
		buffer.setLength(0);
		current = 0;
	}

	@Override
	public void readBuffer(String fileName) {
		// TODO Auto-generated method stub

	}

	@Override
	public int getCurrentLineNumber() {
		return current;
	}

	@Override
	public int goToLine(int n) {
		return current = findLineOffset(n);
	}

	/** Count the number of lines in the buffer */
	@Override
	public int size() {
		int count = 0;
		for (int n = 0; n < buffer.length(); ++n) {
			if (buffer.charAt(n) == '\n')
				++count;
		}
		return count;
	}

	@Override
	public List<String> getLines(int i, int j) {
		// TODO Auto-generated method stub
		return Collections.emptyList();
	}

	/** Undo not supported */
	@Override
	public void undo() {
		throw new UnsupportedOperationException();
	}
	public boolean isUndoSupported() {
		return false;
	}
	
	/** This object as a String == the Buffer as a String */
	@Override
	public String toString() {
		return buffer.toString();
	}

}
