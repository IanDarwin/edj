package edj;

import static org.junit.Assert.assertEquals;

import java.util.Arrays;
import java.util.List;

import javax.swing.JTextArea;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;

@RunWith(Parameterized.class)
public class BufferPrimsTest {

	protected BufferPrims target;

	protected final List<String> THREE_LINES =
			Arrays.asList("One Line", "Another Line", "Third Line");

	public BufferPrimsTest(Class<? extends BufferPrims> clazz) throws Exception {
		if (clazz == BufferPrimsJText.class) {
			target = new BufferPrimsJText(new JTextArea(24, 80));
			return;
		}
		target = clazz.getConstructor().newInstance();
	}
	
	@Parameters(name = "{0}")
	public static Class<BufferPrims>[] params() {
		return (Class<BufferPrims>[]) new Class<?>[] { 
			// BufferPrimsStringBuffer.class, // XXX replace(5 args) unfinished.
			BufferPrimsNoUndo.class, 
			BufferPrimsWithUndo.class,
			BufferPrimsJText.class,
			};
	}

	@Test
	public void testAddOne() {
		target.addLine("Hello world");
		assertEquals(1, target.size());
		if (!target.isUndoSupported()) {
			return;
		}
		target.undo();
		assertEquals(0, target.size());
	}
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

	@Test
	// Test Substitute
	public void testReplaceInOneLine() {
		final String gettysburg = "Fourscore and seven years ago, our fathers brought forth";
		System.out.println(gettysburg.length());
		target.addLine(gettysburg);
		assertEquals(1, target.size());
		target.replace("Fourscore and seven", "87", false);
		assertEquals("replace 1", gettysburg.replace("Fourscore and seven", "87"), target.getCurrentLine());
		target.replace("fathers",  "founders", false);
		target.replace(" ", "_", true);
		assertEquals("replace 1", "87_years_ago,_our_founders_brought_forth", target.getCurrentLine());
	}
	
	@Test
	public void testReplaceInOneLineWithG() {
		final String punk = "One, Two, Three";
		target.addLine(punk);
		assertEquals(1, target.size());
		target.replace("e", "ow", true);
		assertEquals("replaceG", "Onow, Two, Throwow", target.getCurrentLine());
	}

	@Test
	public void testReplaceInAllLines() {
		for (int i = 1; i <= 9; i++)
			target.addLine("Line " + i);

		target.replace("Line", "Type", false, 1, target.size());

		for (int i = 1; i <= target.size(); i++) {
			assertEquals("Type " + i, target.getLine(i));
		}
	}
}
