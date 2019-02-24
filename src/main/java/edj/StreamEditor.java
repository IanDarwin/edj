package edj;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.ArrayList;
import java.util.List;

/**
 * A trivial proof-of-concept for the editing code: show that we can easily
 * implement Unix 'sed' command using the editing code.
 * For now just implements sed's 's' command.
 */
public class StreamEditor {

	static List<ParsedCommand> commands = new ArrayList<>();

	public static void main(String[] args) throws IOException {
		int i;
		for (i = 0; i < args.length; i++) {
			if (args[i].equals("-e")) {
				addCommand(args[++i]);
			} else {
				processFile(args[i], new FileReader(args[i]));
			}
		}
		if (i < args.length) {
			processFile("-", new InputStreamReader(System.in));
		}
	}

	private static void addCommand(String command) {
		// System.out.printf("StreamEditor.addCommand(%s)\n", command);
		ParsedCommand pl = LineParser.parse(command, miniPrims);
		commands.add(pl);
		switch(pl.cmdLetter) {
		case 's': pl.opaque = LineParser.parseSubstitute(pl.operands);
		}
	}
	
	/** A fake BufferPrims for use in the Stream Editor */
	private static BufferPrims miniPrims = new AbstractBufferPrims() {
		
		@Override
		public void readBuffer(String fileName) {
			throw new UnsupportedOperationException();
		}
		
		@Override
		public boolean isUndoSupported() {
			return false;
		}
		
		@Override
		public void addLines(int start, List<String> newLines) {
			throw new UnsupportedOperationException();
		}
	};
	
	private static void processFile(String fileName, Reader r) throws IOException {
		// System.out.printf("StreamEditor.processFile(%s)\n", fileName);
		try (BufferedReader is = new BufferedReader(r)) {
			String buffer;
			while ((buffer = is.readLine()) != null) {
				for (ParsedCommand pl : commands) {
					switch(pl.cmdLetter) {
					case 's':
						ParsedSubstitute ps = (ParsedSubstitute) pl.opaque;
						buffer = ps.global ?
								ps.patt.matcher(buffer).replaceAll(ps.replacement) :
								ps.patt.matcher(buffer).replaceFirst(ps.replacement);
						break;
					default:
						System.err.println("? commdand not implemented");
						break;
					}
				}
				System.out.println(buffer);
			}
		}
	}
}
