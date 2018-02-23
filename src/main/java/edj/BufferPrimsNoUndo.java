package edj;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.List;

public class BufferPrimsNoUndo extends AbstractBufferPrims {
	
	/* (non-Javadoc)
	 * @see edj.BufferPrims#addLines(java.util.List)
	 */
	@Override
	public void addLines(List<String> newLines) {
		addLines(current, newLines);
	}
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
	public void deleteLines(int start, int end) {
		for (int i = start; i <= end; i++)
			buffer.remove(start); // not i!
		current -= (end - start);
	}
	
	public void clearBuffer() {
		current = NO_NUM;
	}
	
	private int nl = 0, nch = 0; // Only accessed single-threadedly

	public void readBuffer(String fileName) throws IOException {
		try (BufferedReader bufferedReader = new BufferedReader(new FileReader(fileName))) {
			bufferedReader.lines().forEach((s) -> {
				nl++; nch += s.length();
				buffer.add(s);
				current++;
			});
		}
		println(String.format("%dL, %dC", nl, nch));
	}
	
	public void printLines(int start, int j) {
		if (current == NO_NUM) {
			System.err.println("No lines in buffer");
			return;
		}
		if (start == NO_NUM) {
			println(buffer.get(current - 1));
			return;
		}
		for (int i = (start == NO_NUM ? 1 : start); i <= j && j < buffer.size(); i++) {
			println(buffer.get(i - 1));
		}
	}
	
	public void undo() {
		System.err.println("?Undo not written yet");
	}
	
	public void println(String s) {
		System.out.println(s);
	}
}
