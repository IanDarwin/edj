package edj;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

/**
 * LineEditor implements a very small subset of the Unix line editor ed(1).
 * It is NOT intended to be a real-world editor; the market for line editors is
 * rather limited, and is already served by ed. It is rather meant just as
 * a rather involved example of the Command pattern, used to implement Undo.
 * 
 * @author Ian Darwin
 */
public class LineEditor {
	
	protected static AbstractBufferPrims buffHandler = new BufferPrimsWithUndo();
	
	protected static BufferedReader in = null;	// command input

	protected static String currentFileName;
	
	/** Should remove throws, use try-catch inside loop */
	public static void main(String[] args) throws IOException {
		String line;
		in = new BufferedReader(new InputStreamReader(System.in));

		if (args.length == 1) {
			currentFileName = args[0];
			buffHandler.readBuffer(currentFileName);
			// Since readBuffer can be used from here or interactively, here we drop its Undoable.
			if (buffHandler instanceof BufferPrimsWithUndo) {
				((BufferPrimsWithUndo)buffHandler).popUndo();
			}
		}

		// The main loop of the editor is right here:
		while ((line = in.readLine())  != null) {

			ParsedLine pl = LineParser.parse(line, buffHandler);
			if (pl == null) {
				System.out.println("?");
				continue;
			}
			EditCommand c = commands[pl.cmdLetter];
			if (c == null) {
				System.out.println("? Unknown command in " + line);
			} else {
				c.execute(pl);
			}
		}
	}
		
	static EditCommand commands[] = new EditCommand[255];
	
	static {
		// Keep in alphabetical order

		// = - print current line number
		commands['='] = pl -> {
			System.err.println(buffHandler.getCurrentLineNumber() + " of " + buffHandler.size());
		};

		// . - print current line
		commands['.'] = pl -> {
			int i = buffHandler.getCurrentLineNumber();
			buffHandler.printLines(i, i);
		};

		// a - append lines
		commands['a'] = pl -> {
			List<String> lines = gatherLines();
			buffHandler.addLines(lines);
		};

		// d - delete lines
		commands['d'] = pl -> {
			buffHandler.deleteLines(pl.startNum, pl.endNum);
		};

		// e - edit a new file
		commands['e'] = pl -> {
			buffHandler.clearBuffer();
			if (!isEmpty(pl.operands)) {
				currentFileName = pl.operands;
			}
			if (currentFileName == null) {
				System.out.println("?no filename");
			} else {
				buffHandler.readBuffer(currentFileName);
			}
		};

		// f - print (or set?) filename
		commands['f'] = pl -> {
			System.out.println(currentFileName == null ? "(no file)" : currentFileName);
		};

		// p - print lines
		commands['p'] = pl -> {
			buffHandler.printLines(pl.startNum, pl.endNum);
		};

		// q - quit the editor
		commands['q'] = pl -> {
			System.exit(0);
		};

		// r - read file into buffer
		// Like e but reads into current buffer w/o setting filename
		commands['r'] = pl -> {
			buffHandler.readBuffer(isEmpty(pl.operands) ? currentFileName : pl.operands);
		};

		// u - undo last undoable
		commands['u'] = pl -> {
			buffHandler.undo();
		};

		// w - write file - maybe someday
		commands['w'] = pl -> {
			System.err.println("?file is read-only");
		};
		
	}
		
	private static boolean isEmpty(String s) {
		return s == null || s.trim().length() == 0;
	}

	/**
	 * Read lines from the user until they type a "." line
	 * @return The List of lines.
	 * @throws IOException 
	 */
	private static List<String> gatherLines() {
		List<String> ret = new ArrayList<>();
		try {
			String line;
			while ((line = in.readLine()) != null && !line.equals(".")) {
				ret.add(line);
			}
		} catch (IOException e) {
			throw new BufferException("IO Error reading from stdin!?", e);
		}
		return ret;
	}
}
