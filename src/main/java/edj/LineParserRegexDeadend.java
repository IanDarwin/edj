package edj;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Newer parser that does a more complete job
 * @author ian
 */
public class LineParser0 {

	protected final static Pattern LINE_REGEX =
		Pattern.compile("(\\d+|.)?(,(\\d+)?)?([a-z=/?\\+\\-])(.*)");

	public static final int LNUM_NONE = -1;
	public static final int LNUM_DOT = -2;
	public static final int LNUM_DOLLAR = Integer.MAX_VALUE;
	
	static ParsedLine parse(String line) {
		final Matcher m = LINE_REGEX.matcher(line);
		final boolean matches = m.matches();
		if (!matches) {
			return null;
		}
		ParsedLine c = new ParsedLine();
		final int nFields = m.groupCount();
		if (nFields != 5) {
			throw new IllegalArgumentException();
		}
		c.startNum = number(m.group(1));
		c.endNum = number(m.group(3));
		c.cmdLetter = m.group(4).charAt(0);
		c.operands = m.group(5);
		return c;
	}
	
	private static int number(String s) {
		if (s == null || s.length() == 0) {
			return LNUM_NONE;	// XXX default
		}
		if (".".equals(s)) {
			return LNUM_DOT;
		}
		if ("$".equals(s)) {
			return LNUM_DOLLAR;
		}
		return Integer.parseInt(s);
	}
}
