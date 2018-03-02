package edj;

import java.util.ArrayList;
import java.util.List;

/**
 * Place for code that is common to all implementations of BufferPrimse
 * @author ian
 *
 */
public abstract class AbstractBufferPrims implements BufferPrims {
	
	protected List<String> buffer = new ArrayList<>();

	/** 
	 * The line number is a human-centric line number, e.g., 
	 * starts at 1 when referring to actual lines,
	 * is zero only when buffer is empty
	 * */
	protected int current = NO_NUM;
	
	public int size() {
		return buffer.size();
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

}
