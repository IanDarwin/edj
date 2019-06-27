package edj;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

/**
 * LineEditor main program - implements a small subset of the Unix line editor ed(1).
 * It is NOT intended to be a real-world editor; the market for line editors is
 * rather limited, and is already served by ed. It is rather meant just as
 * a rather involved example of some design issues that arise in a text editor.
 * 
 * @author Ian Darwin
 */
public class LineEditor {

	protected static BufferPrims buffPrims = new BufferPrimsWithUndo();

	protected static BufferedReader in = null;	// command input

	protected static final boolean debug = false;

	protected static String currentFileName;
	
	static Commands commands = new Commands(buffPrims) {

		/**
		 * Read lines from the user until they type a "." line
		 * @return The List of lines.
		 * @throws IOException 
		 */
		protected List<String> gatherLines() {
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
	};
	
	/** Should remove throws, use try-catch inside loop */
	public static void main(String[] args) throws IOException {
		String line;
		in = new BufferedReader(new InputStreamReader(System.in));

		if (args.length == 1) {
			commands.readFile(currentFileName = args[0]);
			// Since readBuffer can be used from here or interactively, here we drop its Undoable.
			if (buffPrims instanceof UndoManagerEdj) {
				((UndoManagerEdj)buffPrims).popUndo();
			}
		}

		// The main loop of the editor is right here:
		while ((line = in.readLine())  != null) {
			try {
				ParsedCommand pl = LineParser.parse(line, buffPrims);
				if (pl == null) {
					System.out.println("?");
					continue;
				}
				EditCommand c = commands.commands[pl.cmdLetter];
				if (c == null) {
					System.out.println("? Unknown command in " + line);
				} else {
					c.execute(pl);
				}
			} catch (Exception e) {
				System.err.println("? Caught exception " + e);
				if (debug) {
					e.printStackTrace();
				}
			}
		}
	}
}
