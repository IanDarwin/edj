package edj;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class BufferPrimsNoUndo extends AbstractBufferPrims {

	/* (non-Javadoc)
	 * @see edj.BufferPrims#addLines(int, java.util.List)
	 */
	@Override
	public void addLines(int starting, List<String> newLines) {
		buffer.addAll(starting, newLines);
		current += newLines.size();
	}
	
	/* (non-Javadoc)
	 * @see edj.BufferPrims#removeLines(int, int)
	 */
	@Override
	public void deleteLines(int startLnum, int end) {
		// System.out.println("BufferPrimsNoUndo.deleteLines(" + startLnum + ", " + end +")");
		int startIx = lineNumToIndex(startLnum);
		List<String> undoLines = new ArrayList<>();
		for (int i = startIx; i < end; i++) {
			if (buffer.isEmpty()) {
				System.out.println("?Deleted all lines!");
				return;
			}
			undoLines.add(buffer.remove(startIx)); // not i!
		}
		current = startLnum;
	}
	
	public void clearBuffer() {
		current = NO_NUM;
	}
	
	private int nl = 0, nch = 0; // Only accessed single-threadedly

	public void readBuffer(String fileName) {
		try (BufferedReader bufferedReader = new BufferedReader(new FileReader(fileName))) {
			bufferedReader.lines().forEach((s) -> {
				nl++; nch += s.length();
				buffer.add(s);
				current++;
			});
		} catch (FileNotFoundException e) {
			throw new BufferException("File " + fileName + " not found", e);
		} catch (IOException e) {
			throw new BufferException("File " + fileName + " failed during read", e);
		}
		println(String.format("%dL, %dC", nl, nch));
	}
	
	public void writeBuffer(String fileName) {
		throw new UnsupportedOperationException();
	}
	
	@Override
	public void replace(String old, String newStr) {
		// TODO Auto-generated method stub
	}

	@Override
	public void replaceAll(String old, String newStr) {
		// TODO Auto-generated method stub
	}

	@Override
	public void replace(String old, String newStr, int startLine, int endLine) {
		// TODO Auto-generated method stub
	}

	@Override
	public void replaceAll(String old, String newStr, int startLine, int endLine) {
		// TODO Auto-generated method stub
	}
	
	public void undo() {
		throw new UnsupportedOperationException();
	}
	public boolean isUndoSupported() {
		return false;
	}
	
	public void println(String s) {
		System.out.println(s);
	}
}
