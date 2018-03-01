package edj;

public class LineParser {
	
	public static final int 
		LNUM_NONE = Integer.MIN_VALUE,
		LNUM_CUR = -1,
		LNUM_DOLLAR = Integer.MAX_VALUE;

	static ParsedLine parse(String line) {
		if (line == null || (line = line.trim()).length() == 0) {
			return null;
		}
		char[] chars = line.toCharArray();
		ParsedLine cmd = new ParsedLine();
		int i = 0;
		while (i < line.length()) {
			if (chars[i] == '.') {
				cmd.startNum = LNUM_CUR;
				cmd.startFound = true;
				++i;
			}
			if (chars[i] == '$') {
				cmd.startNum = LNUM_DOLLAR;
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
			}

			if (chars[i] == '.') {
				cmd.endNum = LNUM_CUR;
				cmd.endFound = true;
				++i;
			}
			if (chars[i] == '$') {
				cmd.endNum = LNUM_DOLLAR;
				cmd.endFound = true;
				++i;
			}
			while (Character.isDigit(chars[i])) {
				cmd.endNum *= 10;
				cmd.endNum += chars[i++] - '0';
				cmd.endFound = true;
			}

			final char cmdChar = chars[i++];
			if (cmdChar >= 'a' && cmdChar <= 'z')
				cmd.cmdLetter = cmdChar;
			else {
				System.out.printf("LineParser.parse(): failed to parse input %s\n", line);
				return null;
			}
		}
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
}
