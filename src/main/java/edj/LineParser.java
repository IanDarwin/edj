package edj;

import java.util.regex.Pattern;

public class LineParser {
	
	public static final int LNUM_NONE = -1;

	public static ParsedLine parse(String line, BufferPrims buffHandler) {
		if (line == null || (line = line.trim()).length() == 0) {
			return null;
		}
		char[] chars = line.toCharArray();
		ParsedLine cmd = new ParsedLine();
		int i = 0;

			if (chars[i] == '.') {
				cmd.startNum = buffHandler.getCurrentLineNumber();
				cmd.startFound = true;
				++i;
			}
			if (chars[i] == '$') {
				cmd.startNum = buffHandler.size();
				cmd.startFound = true;
				++i;
			}
			while (Character.isDigit(chars[i])) {
				cmd.startNum *= 10;
				cmd.startNum += chars[i++] - '0';
				cmd.startFound = true;
			}

			if (chars[i] == ',')  {
				cmd.commaFound = true;
				++i;
				if (!cmd.startFound) {
					cmd.startFound = true;
					cmd.startNum = 1;
				}
			}

			if (chars[i] == '.') {
				cmd.endNum = buffHandler.getCurrentLineNumber();
				cmd.endFound = true;
				++i;
			}
			if (chars[i] == '$') {
				cmd.endNum = buffHandler.size();
				cmd.endFound = true;
				++i;
			}
			while (Character.isDigit(chars[i])) {
				cmd.endNum *= 10;
				cmd.endNum += chars[i++] - '0';
				cmd.endFound = true;
			}
			
			/** e.g., ",p" or "1,p" */
			if (cmd.commaFound && !cmd.endFound) {
				cmd.endNum = buffHandler.size();
				cmd.endFound = true;
			}
			
			/** Command like "2p", map to "2,2p" */
			if (cmd.startFound && !cmd.endFound) {
				cmd.endNum = cmd.startNum;
				cmd.endFound = true;
			}
			
			/** If neither number found, command like "p" - only applies to current line.
			 */
			if (!cmd.startFound && !cmd.endFound) {
				cmd.startNum = cmd.endNum = buffHandler.getCurrentLineNumber();
				cmd.startFound = cmd.endFound = true;
			}

			final char cmdChar = chars[i++];
			if (cmdChar >= 'a' && cmdChar <= 'z')
				cmd.cmdLetter = cmdChar;
			else {
				System.out.printf("LineParser.parse(): failed to parse input %s (i=%d)\n", line, i);
				return null;
			}
		while (i < line.length() && Character.isWhitespace(chars[i]))
			++i;
		if (i < line.length()) {
			cmd.operands = line.substring(i);
		}
		if (!cmd.startFound) {
			cmd.startNum = LNUM_NONE;
		}
		if (!cmd.endFound) {
			cmd.endNum = LNUM_NONE;
		}
		return cmd;
	}
	
	/**
	 * Parse an ed/sed substitute command
	 * @param commandString The command with the 's' already stripped off
	 * @return a ParsedSubsitute
	 */
	public static ParsedSubstitute parseSubstitute(String commandString) {
		String[] operands = commandString.split(commandString.substring(0, 1));
		// s=abc=def=g results [ "", "abc", "def", "g"]
		if (operands.length == 1) {
			return null;
		}
		ParsedSubstitute subs = new ParsedSubstitute();
		subs.pattStr = operands[1];
		subs.patt = Pattern.compile(subs.pattStr);
		subs.replacement = operands.length > 2 ? operands[2] : "";
		subs.global = operands.length == 4 && operands[3].contains("g");
		subs.print = operands.length == 4 && operands[3].contains("p");
		return subs;
	}
}
