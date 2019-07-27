package edj;

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

	private boolean expected;
	private String input;		// the entire command as a String
	private int lnum1, lnum2;
	private String operands;	// The operand part, e.g, "foo" of "r foo"
	private final static boolean verbose = false;

	private static BufferPrims buffHandler = new AbstractBufferPrims() {
		
		public int getCurrentLineNumber() {
			return 3;
		};
		public int size() {
			return 6;
		}
		
		@Override
		public void addLine(String newLine) {
			// not used
		}

		@Override
		public void addLines(int start, List<String> newLines) {
			// notused
		}

		@Override
		public void readBuffer(String fileName) {
			// notused
		}
		
		public void writeBuffer(String fileName) {
			// notused
		}

		@Override
		public List<String> getLines(int i, int j) {
			// notused
			return null;
		}

		@Override
		public void undo() {
			// notused
		}
		
		@Override
		public boolean isUndoSupported() {
			return false;
		}

	};

	@Before
	public void setUp() throws Exception {
	}

	/** This method provides data to the constructor for use in tests */
	@Parameters(name="{1}")
	public static List<Object[]> data() {
		final int current = buffHandler.getCurrentLineNumber();
		final int size = buffHandler.size();
		return Arrays.asList(new Object[][] {
			{ true, "1,2p", 1, 2,null  },
			{ true, "2p", 2, 2, null },
			{ true, "3s/Line/Foo/", 3, 3, "/Line/Foo/" },
			{ true, "3,6s/Line/Foo/", 3, 6, "/Line/Foo/" },
			{ true, ",p", 1, buffHandler.size(), null  },	// print all
			{ true, ".p", current, current, null  }, // print current
			{ true, "p", current, current, null  },
			{ true, "$p", size, size, null  },
			{ true, "e 3lines.txt", current, current , "3lines.txt"  },
			{ true, "g/foo/s//bar/", current, current, "/foo/s//bar/"  },
			
			// Test some failure modes
			{ false,  "?", LNUM_NONE, LNUM_NONE, null  },	// ?patt? not implemented
			{ false,  "*", 0, 0, null },						// random char
		});
	}

	/** Constructor, gets arguments from data array; cast as needed */
	public LineParserTest(Boolean expected, String input, int lnum1, int lnum2, String operands) {
		this.expected = expected;
		this.input = input;
		this.lnum1 = lnum1;
		this.lnum2 = lnum2;
		this.operands = operands;
	}

	@Test
	public void testPositive() {
		final ParsedCommand parsed = LineParser.parse(input, buffHandler);
		if (expected && parsed == null) {
			fail("Did not parse " + input);
		}
		if (!expected && parsed != null) {
			fail("Should not have parsed " + input);
		}
		if (parsed == null) {
			return;
		}
		if (verbose)
			System.out.println("LineParserTest.testPositive: " + input + " ==> " + parsed);
		assertEquals(lnum1, parsed.startNum);
		assertEquals(lnum2, parsed.endNum);
		if (operands != null) {
			assertEquals(operands, parsed.operands);
		}
	}
}
