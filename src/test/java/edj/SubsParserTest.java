package edj;

import static org.junit.Assert.assertEquals;

import java.util.Arrays;
import java.util.List;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;

@RunWith(Parameterized.class)
public class SubsParserTest {

	static Object[][] params = {
			{"s/him/them/",		"him",	"them",	false, false},
			{"s=him=them=",		"him",	"them",	false, false},
			{"s/him/them/g",	"him",	"them",	true,  false},
			{"s/him/them/p",	"him",	"them",	false, true},
			{"s/him/them/gp",	"him",	"them",	true,  true},
			{"s/him/them/pg",	"him",	"them",	true,  true},
	};
	
	private ParsedSubstitute target;
	private String substCommand;	
	private String pattStr;
	private String replText;
	private boolean expGlobal, expPrint;
	
	@Parameters
	public static List<Object[]> getParams() {
		return Arrays.asList(params);
	}
		
	public SubsParserTest(String substCommand, String pattStr, String replText, boolean expGlobal, boolean expPrint) {
		super();
		this.substCommand = substCommand;
		this.pattStr = pattStr;
		this.replText = replText;
		this.expGlobal = expGlobal;
		this.expPrint = expPrint;
	}

	@Test
	public void testOneParse() {
		target = LineParser.parseSubstitute(substCommand.substring(1));
		assertEquals(pattStr, target.pattStr);
		assertEquals(replText, target.replacement);
		assertEquals(expGlobal, target.global);
		assertEquals(expPrint, target.print);
	}

}
