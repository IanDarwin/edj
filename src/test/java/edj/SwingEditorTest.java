package edj;

import static org.junit.Assert.*;

import org.junit.Before;
import org.junit.Test;

public class SwingEditorTest {
	
	SwingEditor target;
	BufferPrims buffer = new BufferPrimsNoUndo();

	@Before
	public void setUp() throws Exception {
		target = new SwingEditor("Testing");
	}

	private boolean calledBack = false;

	@Test
	public void test() {
		new Commands(buffer).setCommand('d', x -> {
			calledBack = true;
		});
		target.history.addItem("d");
		target.doCommand(null);
		assertTrue("Circuitous test callback didn't work", calledBack);
	}

}
