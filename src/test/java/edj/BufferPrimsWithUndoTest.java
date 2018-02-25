package edj;

import static org.junit.Assert.assertEquals;

import org.junit.Before;
import org.junit.Test;

public class BufferPrimsWithUndoTest extends BufferPrimsTest {

	BufferPrimsWithUndo targetWithUndo;
	
	@Before
	public void setUp() throws Exception {
		target = targetWithUndo = new BufferPrimsWithUndo();
		assertEquals(0, target.getCurrentLineNumber());
	}

	@Test
	public void testAddThenUndo() {
		target.addLines(threeLines);
		targetWithUndo.printTOS();
		assertEquals(3, target.size());
		target.deleteLines(1, 1);
		targetWithUndo.printTOS();
		assertEquals(2, target.size());
		target.undo();	// undo delete
		targetWithUndo.printTOS();
		assertEquals(3, target.size());
		target.undo();	// undo addLines
		targetWithUndo.printTOS();
		assertEquals(0, target.size());
	}

}
