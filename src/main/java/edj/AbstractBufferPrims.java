package edj;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public abstract class AbstractBufferPrims implements BufferPrims {
	
	/** The line number is a human-centric line number, e.g., starts at 1 */
	protected int current = NO_NUM;
	
	private final static Pattern TWO_NUMS = Pattern.compile("^(\\d+),(\\d+)([a-z])$");
	private final static Pattern ONE_NUM = Pattern.compile("^(\\d)([a-z])$");
	
	/** 
	 * For input like "d", ",p", "12d", or "12,14d",
	 * returns an int[3] with 0 = first line # or NO_NUM, 1 = 2nd line# or INF, 3 = cmd
	 */
	int[] getLineRange(String line) {
		int[] range = {BufferPrims.NO_NUM, BufferPrims.INF, '*'};
		final Matcher matcher2 = TWO_NUMS.matcher(line);
		if (matcher2.matches()) {
			range[0] = Integer.parseInt(matcher2.group(1));
			range[1] = Integer.parseInt(matcher2.group(2));
		} else {
			final Matcher matcher1 = ONE_NUM.matcher(line);
			if (matcher1.matches()) {
				range[0] = Integer.parseInt(matcher1.group(1));
			}
		}
		if (range[0] == BufferPrims.NO_NUM && getCurrentLineNumber() != 0) {
			range[0] = 1;
		}
		if (range[1] == BufferPrims.INF && getCurrentLineNumber() != 0) {
			range[1] = getCurrentLineNumber();
		}
		range[2] = line.charAt(line.length() - 1);
		return range;
	}

}
