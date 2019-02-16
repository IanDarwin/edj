package edj;

import static org.junit.Assert.*;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import org.junit.Before;
import org.junit.Test;

/** Only test code specific to the StringBuffer version of BufferPrims */
public class BufferPrimsStringBufferTest {

	private static final String THREE_LINES = "woo\nfoo\nbar\n";
	private static final String FOUR_LINES = "abc\ndef\nghi\nklm\n";
	private static final List<String> LINES_TO_ADD = // Add this to "woo" to get THREE_LINES
		Collections.unmodifiableList(Arrays.asList("foo", "bar"));
	BufferPrimsStringBuffer target;
	
	@Before
	public void setUp() throws Exception {
		target = new BufferPrimsStringBuffer();
	}
	
	@Test
	public void testSetBuffer() {
		target.setBuffer("woo");
		assertEquals("set buffer ends w NL", "woo\n", target.toString());
	}
	
	@Test
	public void testAddLinesAtEnd() {
		target.setBuffer("woo\n");
		target.addLines(LINES_TO_ADD);
		assertEquals("testaddlines", THREE_LINES, target.toString());
	}
	
	@Test
	public void testAddLinesInside() {
		target.setBuffer("before\nafter\n");
		target.addLines(2, Arrays.asList("middle1", "middle2"));
		assertEquals("testAddLinesInside", "before\nmiddle1\nmiddle2\nafter\n", target.toString());
	}

	@Test
	public void testFindLineOffset() {
		target.setBuffer(FOUR_LINES);
		assertEquals("find line", 0, target.findLineOffset(1));
		assertEquals("find line", 4, target.findLineOffset(2));
		assertEquals("find line", 8, target.findLineOffset(3));
		assertEquals("find line", 12, target.findLineOffset(4));
	}
	
	@Test
	public void testLastFindLineByOffset() {
		target.setBuffer(FOUR_LINES);
		int lastLineNum = target.size();
		assertEquals(4, lastLineNum);
		assertEquals("find line", 12, target.findLineOffset(lastLineNum));
	}
	
	@Test
	public void testFindLineBeyondEnd() {
		target.setBuffer(FOUR_LINES);
		assertEquals("find line off end", -1, target.findLineOffset(999));
	}
	
	@Test
	public void testFindLineLength() {
		target.setBuffer("Woot\nHello World\n");
		int len = target.findLineLengthAt(5);
		assertEquals("find line length", 11, len);
	}

	@Test
	public void testDeleteLines() {
		target.setBuffer(FOUR_LINES);
		target.deleteLines(2, 3);
		assertEquals(2, target.size());
	}
}
