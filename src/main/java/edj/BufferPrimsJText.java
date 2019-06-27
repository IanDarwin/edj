package edj;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JTextArea;
import javax.swing.text.BadLocationException;

public class BufferPrimsJText implements BufferPrims {
	
	private JTextArea textView;
	
	BufferPrimsJText(JTextArea textArea) {
		this.textView = textArea;
	}

	@Override
	public void addLine(String newLine) {
		textView.append(newLine);
		textView.append("\n");
		//textView.setCaretPosition(textView.getCaretPosition() + newLine.length());
	}

	/** Append lines to end. Convert to single long string so Undo works. */
	@Override
	public void addLines(List<String> newLines) {
		StringBuilder sb = new StringBuilder();
		newLines.forEach(s->sb.append(s).append('\n'));
		textView.append(sb.toString());
	}

	@Override
	public void addLines(int start, List<String> newLines) {
		try {
			int startOffset = textView.getLineEndOffset(start);
			textView.setCaretPosition(startOffset);
			addLines(newLines);
		} catch (BadLocationException e) {
			throw new RuntimeException(e.toString(), e);
		}
	}

	@Override
	public void deleteLines(int start, int end) {
		try {
			int startOffset = textView.getLineStartOffset(start);
			int endOffset = textView.getLineEndOffset(end);
			textView.select(startOffset, endOffset);
			textView.cut();
		} catch (BadLocationException e) {
			throw new RuntimeException(e.toString(), e);
		}
	}

	@Override
	public void clearBuffer() {
		textView.setText("");
	}

	/**
	 * Read the file into the buffer; making a single long string for Undo
	 * process line-at-a-time to allow adding \n at end of each line
	 */
	@Override
	public void readBuffer(String fileName) {
		StringBuilder sb = new StringBuilder();
		try (BufferedReader rdr = new BufferedReader(new FileReader(fileName))) {
			rdr.lines().forEach(s -> sb.append(s).append('\n'));
			textView.setText(sb.toString());
			System.err.println("bpjt.readBuffer: " + sb);
		} catch (IOException ex) {
			throw new RuntimeException("Error reading file " + fileName, ex);
		}
	}

	@Override
	public int getCurrentLineNumber() {
		int offset = textView.getCaretPosition();
		if (offset > 0)
			--offset; // \n
		try {
			final int lineNumber = textView.getLineOfOffset(offset);
			System.out.printf("getCurrentLineNumber(): offset %d l# %d\n", offset, lineNumber);
			return indexToLineNum(lineNumber);
		} catch (BadLocationException e) {
			throw new RuntimeException(e.toString(), e);
		}
	}

	@Override
	public String getCurrentLine() {
		try {
			int line = textView.getLineOfOffset(textView.getCaretPosition());
			int startOffset = textView.getLineStartOffset(line);
			int endOffset = textView.getLineEndOffset(line) - 1;
			int length = endOffset - startOffset;
			return textView.getText(startOffset, length);
		} catch (BadLocationException e) {
			throw new RuntimeException(e.toString(), e);
		}
	}

	@Override
	public int goToLine(int line) {
		try {
			textView.setCaretPosition(textView.getLineStartOffset(line));
			return line;
		} catch (BadLocationException e) {
			// textView.setCaretPosition(0);
			return 0;
		}
	}

	@Override
	public int size() {
		return textView.getLineCount() - 1;
	}

	@Override
	public String getLine(int line) {
		try {
			int startOffset = textView.getLineStartOffset(line);
			int endOffset = textView.getLineEndOffset(line);
			int length = endOffset - startOffset - 1; // else includes \n
			return textView.getText(startOffset, length);
		} catch (BadLocationException e) {
			throw new RuntimeException(e.toString(), e);
		}
	}

	@Override
	public List<String> getLines(int i, int j) {
		List<String> ret = new ArrayList<>();
		for ( ; i <= j; i++) {
			ret.add(getLine(i));
		}
		return ret;
	}

	@Override
	public void replace(String oldRE, String replacement, boolean all) {
		try {
			final int adjustedLineNum = lineNumToIndex(getCurrentLineNumber());
			System.out.printf("currentLineNum %d, adjusted %d\n",getCurrentLineNumber(), adjustedLineNum);
			int startRange = textView.getLineStartOffset(adjustedLineNum);
			int endRange = textView.getLineEndOffset(adjustedLineNum) - 1;
			System.out.printf("Range %d to %d\n", startRange, endRange);
			String line = textView.getText(startRange, endRange - startRange);
			String str = all ?
					line.replaceAll(oldRE, replacement) :
					line.replaceFirst(oldRE, replacement);
			System.out.printf("replace: %s %s %b--%s %d,%d `%s'\n",
					oldRE, replacement, all,
					line, startRange, endRange, str);
			textView.replaceRange(str, startRange, endRange);
		} catch (BadLocationException e) {
			throw new RuntimeException(e.toString(), e);
		}
	}

	@Override
	public void replace(String oldRE, String newStr, boolean all, int startLine, int endLine) {
		// TODO Auto-generated method stub

	}

	@Override
	public boolean isUndoSupported() {
		return false; // for now - should be true;
	}
	
	@Override
	public void undo() {
		System.out.println("BufferPrimsJText.undo()");
	}
	
	// @Override
	public void pushUndo(String description, Runnable undoer) {
		System.out.println("BufferPrimsJText.pushUndo()");
	}

}
