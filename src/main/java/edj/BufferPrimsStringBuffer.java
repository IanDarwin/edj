package edj;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Implement BufferPrims with a single long string with \n delimiting lines.
 * Not used in the current editor but may be useful as an existence proof.
 * Has some additional methods appropriate to its internal representation.
 * The name is historical; the code has been changed to use StringBuilder
 * but it's annoying to rename the class...
 * @author Ian Darwin
 */
public class BufferPrimsStringBuffer implements BufferPrims {

	private StringBuilder buffer = new StringBuilder();
	int current;

	protected String getBuffer() {
		return buffer.toString();
	}

	protected void setBuffer(String contents) {
		current = 0;
		this.buffer = new StringBuilder(contents);
		// Last line must end with newline
		if (buffer.charAt(buffer.length() - 1) != '\n')
			buffer.append('\n');
	}

	@Override
	public void addLine(String s) {
		buffer.append(s).append('\n');
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
	public void deleteLines(int startLine, int endLine) {
		if (startLine > endLine) {
			throw new IllegalArgumentException();
		}
		int startOffset = findLineOffset(startLine),
				endOffset = findLineOffset(endLine);
		buffer.delete(startOffset, endOffset + findLineLengthAt(endOffset) + 1);
	}

	@Override
	public void clearBuffer() {
		buffer.setLength(0);
		current = 0;
	}

	@Override
	public void readBuffer(String fileName) {
		// process line-at-a-time to ensure only \n at end of each line
		try (BufferedReader rdr = new BufferedReader(new FileReader(fileName))) {
			rdr.lines().forEach(this::addLine);
		} catch (IOException ex) {
			throw new RuntimeException("Error reading file " + fileName, ex);
		}
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
		int startOffset = findLineOffset(current);
		int len = findLineLengthAt(startOffset);
		return buffer.substring(startOffset, startOffset + len);
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
		int startOffset = findLineOffset(current);
		int length = findLineLengthAt(startOffset);
		String tmp = buffer.substring(startOffset, length);
		tmp = all ? tmp.replaceAll(old, newStr) : tmp.replace(old, newStr);
		buffer.replace(startOffset, length, tmp);
	}

	@Override
	public void replace(String old, String newStr, boolean all, int startLine, int endLine) {
		int startOffset = findLineOffset(startLine),
				endOffset = findLineOffset(endLine + 1);
		int length = endOffset - startOffset;
		String tmp = buffer.substring(startOffset, length);
		tmp = all ? tmp.replaceAll(old, newStr) : tmp.replace(old, newStr);
		buffer.replace(startOffset, length, tmp);
	}

	/** Undo not supported */
	@Override
	public boolean isUndoSupported() {
		return false;
	}

	/** Format the Buffer as a single long String */
	@Override
	public String toString() {
		return buffer.toString();
	}

}
