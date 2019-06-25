package edj;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
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
	
	public void clearBuffer() {
		buffer.clear();
		current = NO_NUM;
	}
	
	private int nl = 0, nch = 0; // Only accessed single-threadedly

	public void readBuffer(String fileName) {
		try (BufferedReader bufferedReader = new BufferedReader(new FileReader(fileName))) {
			clearBuffer();
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
