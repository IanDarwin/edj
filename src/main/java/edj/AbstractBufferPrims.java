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
	 * @see edj.BufferPrims#addLines(java.util.List)
	 */
	@Override
	public void addLines(List<String> newLines) {
		addLines(current, newLines);
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
}
