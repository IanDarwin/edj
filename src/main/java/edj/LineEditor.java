package edj;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

/**
 * LineEditor main program - implements a very small subset of the Unix line editor ed(1).
 * It is NOT intended to be a real-world editor; the market for line editors is
 * rather limited, and is already served by ed. It is rather meant just as
 * a rather involved example of some design issues that arise in a text editor.
 * 
 * @author Ian Darwin
 */
public class LineEditor {
	
	protected static AbstractBufferPrims buffPrims = new BufferPrimsWithUndo();
	
	protected static BufferedReader in = null;	// command input

	protected static String currentFileName;
	
	/** Should remove throws, use try-catch inside loop */
	public static void main(String[] args) throws IOException {
		String line;
		in = new BufferedReader(new InputStreamReader(System.in));

		if (args.length == 1) {
			readFile(currentFileName = args[0]);
			// Since readBuffer can be used from here or interactively, here we drop its Undoable.
			if (buffPrims instanceof BufferPrimsWithUndo) {
				((BufferPrimsWithUndo)buffPrims).popUndo();
			}
		}

		// The main loop of the editor is right here:
		while ((line = in.readLine())  != null) {

			ParsedLine pl = LineParser.parse(line, buffPrims);
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
			System.err.println(buffPrims.getCurrentLineNumber() + " of " + buffPrims.size());
		};

		// . - print current line
		commands['.'] = pl -> {
			int i = buffPrims.getCurrentLineNumber();
			buffPrims.getLines(i, i);
		};

		// a - append lines
		commands['a'] = pl -> {
			List<String> lines = gatherLines();
			buffPrims.addLines(lines);
		};

		// d - delete lines
		commands['d'] = pl -> {
			buffPrims.deleteLines(pl.startNum, pl.endNum);
		};

		// e - edit a new file
		commands['e'] = pl -> {
			buffPrims.clearBuffer();
			if (!isEmpty(pl.operands)) {
				currentFileName = pl.operands;
			}
			readFile(currentFileName);
		};

		// f - print (or set?) filename
		commands['f'] = pl -> {
			if (!isEmpty(pl.operands)) {
				currentFileName = pl.operands;
			}
			System.out.println(currentFileName == null ? "(no file)" : currentFileName);
		};

		// p - print lines
		commands['p'] = pl -> {
			buffPrims.getLines(pl.startNum, pl.endNum).forEach(System.out::println);
		};

		// q - quit the editor
		commands['q'] = pl -> {
			System.exit(0);
		};

		// r - read file into buffer
		// Like e but reads into current buffer w/o setting filename
		commands['r'] = pl -> {
			buffPrims.readBuffer(isEmpty(pl.operands) ? currentFileName : pl.operands);
		};
		
		// s - substitute s/old/new/ - old may be regex, new is string
		commands['s'] = pl -> {
			// Figure out line range
			int[] range = pl.lineRange();
			// Figure out rest of line, should be something like /oldRE/newStr/[g]
			// Any char not in the two strings can be used as delimiter
			String[] operands = pl.operands.split(pl.operands.substring(0, 1));
			// s=abc=def=g results [ "", "abc", "def", "g"]
			if (operands.length == 1) {
				System.out.println("? s/oldRe/newStr/[g]");
			}
			String oldStr = operands[1];
			String newStr = operands.length > 2 ? operands[2] : "";
			boolean global = operands.length == 4 && operands[3].contains("g");
			boolean print = operands.length == 4 && operands[3].contains("p");
			if (range.length == 0) {			// current line only
				buffPrims.replace(oldStr, newStr, global);
				if (print) {
					System.out.println(buffPrims.getCurrentLine());
				}
			} else {							// replace across range of lines
				buffPrims.replace(oldStr, newStr, global, range[0], range[1]);
				if (print) {
					System.out.println(buffPrims.getCurrentLine());
				}
			}
		};

		// u - undo last undoable
		commands['u'] = pl -> {
			if (buffPrims.isUndoSupported()) {
				buffPrims.undo();
			} else {
				System.out.println("?Undo not supported");
			}
		};

		// w - write file - maybe someday
		commands['w'] = pl -> {
			System.err.println("?file is read-only");
		};
		
	}

	public static void readFile(String fileName) {
		if (fileName == null) {
			System.out.println("?no filename");
		} else {
			File f = new File(fileName);
			if (f.canRead()) {
				buffPrims.readBuffer(fileName);
			} else {
				System.out.println("File not readable");
			}
		}
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
