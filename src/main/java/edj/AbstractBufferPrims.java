package edj;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Place for code that is common to all List<String>-based implementations of BufferPrimse
 * @author Ian Darwin
 */
public abstract class AbstractBufferPrims implements BufferPrims {
	
	protected List<String> buffer = new ArrayList<>();

	/** 
	 * The line number is a human-centric line number, e.g., 
	 * starts at 1 when referring to actual lines,
	 * is zero only when buffer is empty
	 * */
	protected int current = NO_NUM;
	
	/* (non-Javadoc)
	 * @see edj.BufferPrims#size()
	 */
	public int size() {
		return buffer.size();
	}
	
	/* (non-Javadoc)
	 * @see edj.BufferPrims#clearBuffer()
	 */
	@Override
	public void clearBuffer() {
		// notused
	}

	/* (non-Javadoc)
	 * @see edj.BufferPrims#addLines(java.util.List)
	 */
	@Override
	public void addLines(List<String> newLines) {
		addLines(current, newLines);
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

	@Override
	public String getCurrentLine() {
		return buffer.get(lineNumToIndex(current));
	}

	@Override
	public int getCurrentLineNumber() {
		return current;
	}

	protected int lineNumToIndex(int ln) {
		if (ln == 0) {
			ln = 1;
		}
		return ln - 1;
	}
	protected int indexToLineNum(int ix) {
		return ix + 1;
	}

	public int goToLine(int ln) {
		if (current == NO_NUM) {
			return NO_NUM;
		}
		int ix = lineNumToIndex(ln);
		if (ix > buffer.size())
			ix = buffer.size();
		return current = ln;
	}
	
	public String getLine(int ln) {
		return buffer.get(lineNumToIndex(ln));
	}

	public List<String> getLines(int start, int end) {
		if (buffer.size() == 0) {
			return Collections.emptyList();
		}
		List<String> ret = new ArrayList<>();
		for (int i = start; i <= end && i <= buffer.size(); i++) {
			ret.add(buffer.get(lineNumToIndex(i)));
		}
		return ret;
	}
	
	@Override
	public void replace(String oldRE, String newStr, boolean all) {
		int ix = lineNumToIndex(current);
		String target = buffer.get(ix);
		buffer.set(lineNumToIndex(ix), all?
			target.replaceAll(oldRE, newStr) :
			target.replaceFirst(oldRE, newStr));
	}

	@Override
	public void replace(String oldRE, String newStr, boolean all, int startLine, int endLine) {
		for (int ix = startLine; ix <= endLine; ix++) {
			String target = buffer.get(lineNumToIndex(ix));
			buffer.set(lineNumToIndex(ix), all?
					target.replaceAll(oldRE, newStr) :
					target.replaceFirst(oldRE, newStr));
		}
	}
}
