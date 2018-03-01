package edj;

import static edj.LineParser.LNUM_CUR;
import static edj.LineParser.LNUM_DOLLAR;
import static edj.LineParser.LNUM_NONE;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import java.util.Arrays;
import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;

@RunWith(value=Parameterized.class)
public class LineParserTest {

	@Before
	public void setUp() throws Exception {
	}

	/** This method provides data to the constructor for use in tests */
	@Parameters
	public static List<Object[]> data() {
		return Arrays.asList(new Object[][] {
			{ true,  "1,2p", 1, 2 },
			{ true,  ",p", LNUM_NONE, LNUM_NONE },	// print all
			{ true,  ".p", LNUM_CUR, LNUM_NONE  },// print current
			{ true,  "p", LNUM_NONE, LNUM_NONE  },
			{ true,  "$p", LNUM_DOLLAR, LNUM_NONE  },
			{ false,  "?", LNUM_NONE, LNUM_NONE  },
			//{ true, "g/foo/s//bar/", 0, 0  },
			{ false,  "*", 0, 0  },
		});
	}

	private boolean expected;
	String input;
	private int lnum1, lnum2;

	/** Constructor, gets arguments from data array; cast as needed */
	public LineParserTest(Boolean expected, String input, int lnum1, int lnum2) {
		this.expected = expected;
		this.input = input;
		this.lnum1 = lnum1;
		this.lnum2 = lnum2;
	}

	@Test
	public void testPositive() {
		final ParsedLine parsed = LineParser.parse(input);
		if (expected && parsed == null) {
			fail("Did not parse " + input);
		}
		if (!expected && parsed != null) {
			fail("Should not have parsed " + input);
		}
		if (parsed == null) {
			return;
		}
		System.out.println("LineParserTest.testPositive: " + input + " ==> " + parsed);
		assertEquals(lnum1, parsed.startNum);
		assertEquals(lnum2, parsed.endNum);
	}
}
