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
	class UndoableCommand {
		public UndoableCommand(String name, Runnable r) {
			this.name = name;
			this.r = r;
		}
		String name;
		protected Runnable r;
	}
	
	Stack<UndoableCommand> undoables = new Stack<>();
	
	private void pushUndo(String name, Runnable r) {
		undoables.push(new UndoableCommand(name, r));
	}
	
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
	public void addLines(int start, List<String> newLines) {
		buffer.addAll(start, newLines);
		current += newLines.size();
		pushUndo("add", () -> deleteLines(start, start + newLines.size()));
	}
	
	/* (non-Javadoc)
	 * @see behavioral.BufferPrims#removeLines(int, int)
	 */
	@Override
	public void deleteLines(int start, int end) {
		List<String> undoLines = new ArrayList<>();
		for (int i = start; i <= end; i++)
			undoLines.add(buffer.remove(start)); // not i!
		current -= (end - start);
		pushUndo("delete", () -> addLines(start, undoLines));
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
		pushUndo("read", () -> deleteLines(startLine, startLine + nl));
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
	
	/** If there are any undoable actions, pop the top one and run it. */
	public void undo() {
		if (undoables.empty()) {
			println("?Nothing to undo");
			return;
		}
		UndoableCommand undoable = undoables.pop();
		System.out.println("Undoing " + undoable.name);
		undoable.r.run();
		if (undoables.empty()) {
			undoables.pop();		// all actions create undos, drop them so undo works normally
		}
	}
	
	public void println(String s) {
		System.out.println(s);
	}
}
