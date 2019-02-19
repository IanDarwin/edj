package edj;

import java.util.regex.Pattern;

/** 
 * Represents a substitute command.
 * s/patt/replacement/gp
 */
public class ParsedSubstitute {
	Pattern patt;
	String pattStr;
	String replacement;
	boolean global;
	boolean print;
}
