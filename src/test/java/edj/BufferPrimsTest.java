package edj;

import static org.junit.Assert.assertEquals;

import java.util.Arrays;
import java.util.List;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;

@RunWith(Parameterized.class)
public class BufferPrimsTest {

	protected BufferPrims target;

	public BufferPrimsTest(Class<BufferPrims> clazz) throws Exception {
		target = clazz.newInstance();
	}
	
	@Parameters(name = "{0}")
	public static Class<BufferPrims>[] params() {
		return (Class<BufferPrims>[]) new Class<?>[] { 
			BufferPrimsStringBuffer.class, 
			BufferPrimsNoUndo.class, 
			BufferPrimsWithUndo.class };
	}

	protected final List<String> THREE_LINES = Arrays.asList("One Line", "Another Line", "Third Line");

	@Test
	public void testAdd() {
		target.addLines(THREE_LINES);
		assertEquals(3, target.size());
		assertEquals(3, target.getCurrentLineNumber());
		target.addLines(Arrays.asList("Third Line"));
		assertEquals(4, target.getCurrentLineNumber());
	}

	@Test
	public void testAdd3ThenDelete1() {
		target.addLines(THREE_LINES);
		assertEquals(3, target.size());
		target.deleteLines(1, 1);
		assertEquals(2, target.size());
	}

	@Test
	public void testAdd3ThenDelete3() {
		target.addLines(THREE_LINES);
		assertEquals(3, target.size());
		target.deleteLines(1, 3);
		assertEquals(0, target.size());
	}
	
	@Test
	public void testAddThenUndo() {
		if (!target.isUndoSupported()) {
			return;
		}
		target.addLines(THREE_LINES);
		((BufferPrimsWithUndo) target).printTOS();
		assertEquals(3, target.size());
		target.deleteLines(1, 1);
		((BufferPrimsWithUndo) target).printTOS();
		assertEquals(2, target.size());
		target.undo();	// undo delete
		((BufferPrimsWithUndo) target).printTOS();
		assertEquals(3, target.size());
		target.undo();	// undo addLines
		((BufferPrimsWithUndo) target).printTOS();
		assertEquals(0, target.size());
	}
	
	@Test
	public void getGetOneLine() {
		target.addLines(THREE_LINES);
		String actual = target.getLine(2);
		assertEquals("getline(2)", "Another Line", actual);
		actual = target.getLine(3);
		assertEquals("Third Line", actual);
	}
	
	@Test
	public void testGetLines() {
		target.addLines(THREE_LINES);
		assertEquals(3, target.size());
		final List<String> ret = target.getLines(2, 3);
		assertEquals(2, ret.size());
		assertEquals("Another Line", ret.get(0));
		assertEquals("Third Line", ret.get(1));
	}
}
