package edj;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Stack;

public class BufferPrimsWithUndo extends AbstractBufferPrims {
	
	// You don't actually need this in a non-GUI application
	enum UndoableOp {
		INSERT,
		DELETE
	}
	abstract interface UndoableCommand extends Runnable {
		// "I have nothing to add"
	}
	
	Stack<UndoableCommand> undoables = new Stack<>();
	
	/* (non-Javadoc)
	 * @see behavioral.BufferPrims#addLines(java.util.List)
	 */
	@Override
	public void addLines(List<String> newLines) {
		addLines(current, newLines);
	}
	/* (non-Javadoc)
	 * @see behavioral.BufferPrims#addLines(int, java.util.List)
	 */
	@Override
	public void addLines(int starting, List<String> newLines) {
		buffer.addAll(starting, newLines);
		current += newLines.size();
		undoables.add(() -> deleteLines(starting, starting + newLines.size()));
	}
	
	/* (non-Javadoc)
	 * @see behavioral.BufferPrims#removeLines(int, int)
	 */
	@Override
	public void deleteLines(int start, int end) {
		List<String> undoLines = new ArrayList<>();
		for (int i = start; i < end; i++)
			undoLines.add(buffer.remove(start)); // not i!
		current -= (end - start);
		undoables.add(() -> addLines(start, undoLines));
	}
	
	/* (non-Javadoc)
	 * @see behavioral.BufferPrims#getCurrentLineNum()
	 */
	@Override
	public int getCurrentLineNumber() {
		return current;
	}
	
	public void clearBuffer() {
		current = NO_NUM;
		buffer.clear();
		undoables.clear();		// can't undo after this!
	}
	
	private int nl = 0, nch = 0; // Only accessed single-threadedly, only from readBuffer

	public void readBuffer(String fileName) throws IOException {
		int startLine = current;
		try (BufferedReader bufferedReader = new BufferedReader(new FileReader(fileName))) {
			bufferedReader.lines().forEach((s) -> {
				nl++; nch += s.length();
				buffer.add(s);
				current++;
			});
		}
		println(String.format("%dL, %dC", nl, nch));
		undoables.add(() -> deleteLines(startLine, startLine + nl));
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
		if (undoables.empty()) {
			println("?Nothing to undo");
			return;
		}
		undoables.pop().run();
	}
	
	public void println(String s) {
		System.out.println(s);
	}
}
