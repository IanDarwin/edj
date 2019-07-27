package edj;

import java.io.File;

public class Commands {
	static EditCommand commands[] = new EditCommand[255];
	static BufferPrims buffPrims = null;
	protected static String currentFileName;
	
	Commands(BufferPrims buffPrims) {
		this.buffPrims = buffPrims;
		fillCommands();
	}

	public void readFile(String fileName) {
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
	
	private void fillCommands() {
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
			throw new UnsupportedOperationException("Editor must provide 'a' implementation");
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
			final String commandString = pl.operands;

			ParsedSubstitute subs = LineParser.parseSubstitute(commandString);
			if (!pl.startFound && range.length == 0) {			// current line only
				buffPrims.replace(subs.pattStr, subs.replacement, subs.global);
				if (subs.print) {
					System.out.println(buffPrims.getCurrentLine());
				}
			} else {							// replace across range of lines
				buffPrims.replace(subs.pattStr, subs.replacement, subs.global, range[0], range[1]);
				if (subs.print) {
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

	/**
	 * Allow the main editor to add/replace a command.
	 * Use with extreme caution!
	 * @param ch The letter for the character
	 * @param r The Edit Command to use for that character
	 */
	public void setCommand(char ch, EditCommand r) {
		commands[ch] = r;
	}

	private static boolean isEmpty(String s) {
		return s == null || s.trim().length() == 0;
	}
}
