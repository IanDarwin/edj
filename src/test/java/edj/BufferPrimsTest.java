package edj;

import static org.junit.Assert.assertEquals;

import java.util.Arrays;
import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;

@RunWith(Parameterized.class)
public class BufferPrimsTest {

	private Class<BufferPrims> clazz;

	public BufferPrimsTest(Class<BufferPrims> clazz) {
		this.clazz = clazz;
	}
	
	@Parameters
	public static Class<BufferPrims>[] params() {
		return (Class<BufferPrims>[]) new Class<?>[] { BufferPrimsStringBuffer.class, BufferPrimsNoUndo.class, BufferPrimsWithUndo.class };
	}
	
	protected BufferPrims target;
	
	@Before
	public void setup() throws Exception {
		target = clazz.newInstance();
	}

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
	public void testAdd3ThenDelete1() {
		target.addLines(threeLines);
		assertEquals(3, target.size());
		target.deleteLines(1, 1);
		assertEquals(2, target.size());
	}

	@Test
	public void testAdd3ThenDelete3() {
		target.addLines(threeLines);
		assertEquals(3, target.size());
		target.deleteLines(1, 3);
		assertEquals(0, target.size());
	}
	
	@Test
	public void testAddThenUndo() {
		if (!target.isUndoSupported()) {
			return;
		}
		target.addLines(threeLines);
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
}
