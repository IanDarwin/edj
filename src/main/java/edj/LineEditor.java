package edj;

import java.io.Console;
import java.io.IOException;
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
	
	protected static AbstractBufferPrims buffHandler = new BufferPrimsNoUndo();
	
	static Console console = System.console();
	
	/** Should remove throws, use try-catch inside loop */
	public static void main(String[] args) throws IOException {
		String line;
		if (console == null) {
			System.err.println("Can't use Console class, r u running under an IDE?");
			return;
		}
		if (args.length == 1) {
			buffHandler.readBuffer(args[0]);
		}
		while ((line = console.readLine())  != null) {
			// System.out.println("Line is: " + line);
			
			// FILE RELATED
			if (line.startsWith("e")) {
				buffHandler.clearBuffer();
				buffHandler.readBuffer(line.substring(1).trim());
				continue;
			}
			// We could add 'w' if feeling brave
			if (line.equals("q")) {
				System.exit(0);
			}
			
			// BUFFER-RELATED
			if (line.equals("=")) {
				System.err.println(buffHandler.getCurrentLineNumber());
				continue;
			}
			if (line.equals("a")) {
				List<String> lines = gatherLines();
				buffHandler.addLines(lines);
				continue;
			}
			if (line.endsWith("d")) {
				int[] range = buffHandler.getLineRange(line);
				buffHandler.deleteLines(range[0], range[1]);
				continue;
			}
			if (line.endsWith("p")) {
				int[] range = buffHandler.getLineRange(line);
				buffHandler.printLines(range[0], range[1]);
				continue;
			}
			if (line.equals(".")) {
				int i = buffHandler.getCurrentLineNumber();
				buffHandler.printLines(i, i);
				continue;
			}
			if (line.equals("u")) {
				buffHandler.undo();
				continue;
			}
			// default: standard 'ed' error handling
			System.out.println("?");
		}
	}

	/**
	 * Read lines from the user until they type a "." line
	 * @return The List of lines.
	 */
	private static List<String> gatherLines() {
		List<String> ret = new ArrayList<>();
		String line;
		while ((line = console.readLine()) != null && !line.equals(".")) {
			ret.add(line);
		}
		return ret;
	}
}
