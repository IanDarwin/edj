package edj;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

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
		// Last line must end with newline
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

	static final Pattern linesPatt = Pattern.compile(".*?\n");

	int findLineOffset(int startLineNum) {
		if (buffer == null) {
			throw new NullPointerException("findLine called with no buffer");
		}
		Matcher matcher = linesPatt.matcher(buffer);
		int offset = 0;
		for (int i = 1; i < startLineNum; i++) {
			if (!matcher.find()) {
				System.err.println("No line found for line " + i);
				return -1;
			}
			offset += matcher.group(0).length();
		}
		return offset;
	}

	int findLineLengthAt(int startOffset) {
		int offset;
		for (offset = startOffset; buffer.charAt(offset) != '\n' && offset < buffer.length(); ++offset)
			;// Do nothing; it's all in the loop;
		return offset - startOffset;
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
	public String getCurrentLine() {
		// TODO
		return null;
	}

	@Override
	public String getLine(int ln) {
		int startOffset = findLineOffset(ln);
		int len = findLineLengthAt(startOffset);
		return buffer.substring(startOffset, startOffset + len);
	}

	@Override
	public List<String> getLines(int start, int end) {
		if (end < start) {
			throw new IllegalArgumentException();
		}
		if (buffer == null || buffer.length() == 0)
		return Collections.emptyList();
		List<String> ret = new ArrayList<>();
		for (int i = start; i <= end; i++) {
			ret.add(getLine(i));
		}
		return ret;
	}

	@Override
	public void replace(String old, String newStr, boolean all) {
		// TODO Auto-generated method stub
	}

	@Override
	public void replace(String old, String newStr, boolean all, int startLine, int endLine) {
		// TODO Auto-generated method stub
	}

	/** Undo not supported */
	@Override
	public boolean isUndoSupported() {
		return false;
	}
	@Override
	public void undo() {
		throw new UnsupportedOperationException();
	}

	/** This object as a String == the Buffer as a String */
	@Override
	public String toString() {
		return buffer.toString();
	}

}
