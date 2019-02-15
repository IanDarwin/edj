package edj;

import static org.junit.Assert.*;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import org.junit.Before;
import org.junit.Test;

public class BufferPrimsStringBufferTest {

	private static final String THREE_LINES = "woo\nfoo\nbar\n";
	private static final String FOUR_LINES = "abc\ndef\nghi\nklm\n";
	private static final List<String> LINES_TO_ADD = Collections.unmodifiableList(Arrays.asList("foo", "bar"));
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
		target.addLines(1, Arrays.asList("middle1", "middle2"));
		assertEquals("testAddLinesInside", "before\nmiddle1\nmiddle2\nafter\n", target.toString());
	}

	@Test
	public void testFindLine() {
		target.setBuffer(FOUR_LINES);
		assertEquals("find line", 4, target.findLineOffset(2));
	}
	
	@Test
	public void testFindLineOffEnd() {
		target.setBuffer(FOUR_LINES);
		assertEquals("find line off end", -1, target.findLineOffset(999));
	}

}
