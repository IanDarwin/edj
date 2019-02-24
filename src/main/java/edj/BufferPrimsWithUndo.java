package edj;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Stack;

public class BufferPrimsWithUndo extends AbstractBufferPrims implements UndoManager {

	class UndoableCommand {
		public UndoableCommand(String name, Runnable r) {
			this.name = name;
			this.r = r;
		}
		String name;
		protected Runnable r;
	}
	
	Stack<UndoableCommand> undoables = new Stack<>();
	
	public void pushUndo(String name, Runnable r) {
		undoables.push(new UndoableCommand(name, r));
	}

	/* (non-Javadoc)
	 * @see edj.UndoManager#popUndo()
	 */
	@Override
	public void popUndo() {
		if (!undoables.isEmpty()) {
			undoables.pop();
		}
	}

	public void printTOS() {
		// System.out.println("Undo TOS: " + (undoables.isEmpty() ? "(empty)" : undoables.peek().name));
	}
	
	@Override
	public void clearBuffer() {
		current = NO_NUM;
		buffer.clear();
		undoables.clear();		// can't undo after this!
	}
	
	@Override
	public void addLine(String newLine) {
		super.addLine(newLine);
		pushUndo("add 1 line", () -> deleteLines(current, current));
	}

	/* (non-Javadoc)
	 * @see edj.BufferPrims#addLines(int, java.util.List)
	 */
	@Override
	public void addLines(int startLnum, List<String> newLines) {
		// System.out.printf("BufferPrimsWithUndo.addLines(): start %d, size %d%n", startLnum, newLines.size());
		int startIx = lineNumToIndex(startLnum);
		buffer.addAll(startIx, newLines);
		current += newLines.size();
		pushUndo("add " + newLines.size() + " lines", () -> deleteLines(startLnum, startLnum + newLines.size()));
	}
	
	/* (non-Javadoc)
	 * @see edj.BufferPrims#removeLines(int, int)
	 */
	@Override
	public void deleteLines(int startLnum, int end) {
		// System.out.println("BufferPrimsWithUndo.deleteLines(" + startLnum + ", " + end +")");
		int startIx = lineNumToIndex(startLnum);
		List<String> undoLines = new ArrayList<>();
		for (int i = startIx; i < end; i++) {
			// System.out.println("BufferPrimsWithUndo.deleteLines(): inner:");
			if (buffer.isEmpty()) {
				System.out.println("?Deleted all lines!");
				break;
			}
			undoLines.add(buffer.remove(startIx)); // not i!
		}
		current = startLnum;
		if (!undoLines.isEmpty()) {
			pushUndo("delete lines " + startLnum + " to " + end, 
				() -> addLines(startLnum, undoLines));
		}
	}
	
	private int nl = 0, nch = 0; // Only accessed single-threadedly, only from readBuffer

	@Override
	public void readBuffer(String fileName) {
		int startLine = current;
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
		pushUndo("read", () -> deleteLines(startLine, startLine + nl));
	}
	
	@Override
	public void writeBuffer(String fileName) {
		throw new UnsupportedOperationException();
	}

	/* (non-Javadoc)
	 * @see edj.UndoManager#isUndoSupported()
	 */
	@Override
	public boolean isUndoSupported() {
		return true;
	}
	/* (non-Javadoc)
	 * @see edj.UndoManager#undo()
	 */
	@Override
	public void undo() {
		if (undoables.empty()) {
			println("?Nothing to undo");
			return;
		}
		UndoableCommand undoable = undoables.pop();
		// System.out.println("Undoing " + undoable.name);
		undoable.r.run();
		if (!undoables.empty()) {
			undoables.pop();		// all actions create undos, drop them so undo works normally
		}
	}
	
	public void println(String s) {
		System.out.println(s);
	}
}
