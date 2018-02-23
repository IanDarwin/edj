package edj;

import static org.junit.Assert.assertEquals;

import org.junit.Before;
import org.junit.Test;

public class BufferPrimsWithUndoTest extends BufferPrimsTest {

	@Before
	public void setUp() throws Exception {
		target = new BufferPrimsWithUndo();
		assertEquals(0, target.getCurrentLineNumber());
	}

	@Test
	public void testAddThenUndo() {
		target.addLines(threeLines);
		assertEquals(3, target.size());
		target.deleteLines(1, 1);
		assertEquals(2, target.size());
		target.undo();	// undo delete
		assertEquals(3, target.size());
		target.undo();	// undo addLines
		System.out.println(((BufferPrimsWithUndo)target).undoables.peek().name);
		assertEquals(0, target.size());
	}

}
