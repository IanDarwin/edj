package edj;

import static org.junit.Assert.assertEquals;

import java.util.Arrays;
import java.util.List;

import org.junit.Before;
import org.junit.Test;

public class BufferPrimsNoUndoTest extends BufferPrimsTest {

	@Before
	public void setUp() throws Exception {
		target = new BufferPrimsNoUndo();
		assertEquals(0, target.getCurrentLineNumber());
	}

	@Test
	public void testAdd() {
		List<String> lines = Arrays.asList("Line", "Another Line");
		target.addLines(lines);
		assertEquals(2, target.getCurrentLineNumber());
		target.addLines(Arrays.asList("Third Line"));
		assertEquals(3, target.getCurrentLineNumber());
	}

}
