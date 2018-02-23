package edj;

import static org.junit.Assert.assertEquals;

import java.util.Arrays;
import java.util.List;

import org.junit.Test;

public abstract class BufferPrimsTest {

	protected BufferPrims target;

	protected List<String> threeLines = Arrays.asList("One Line", "Another Line", "Third Line");

	@Test
	public void testAdd() {
		target.addLines(threeLines);
		assertEquals(3, target.size());
		assertEquals(3, target.getCurrentLineNumber());
		target.addLines(Arrays.asList("Third Line"));
		assertEquals(4, target.getCurrentLineNumber());
	}

	@Test
	public void testAddThenDelete() {
		target.addLines(threeLines);
		assertEquals(3, target.size());
		target.deleteLines(1, 1);
		assertEquals(2, target.size());
	}

}
