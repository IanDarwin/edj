package edj;

import static org.junit.Assert.assertEquals;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class LineEditorTest {

	private InputStream in;
	private BufferPrims buffer = LineEditor.buffPrims;

	@Before
	public void setUp() throws Exception {
		in = System.in;
	}
	
	@After
	public void cleanUpUgly() throws Exception {
		System.setIn(in);
	}
	
	private void openCommandString(String commands) throws IOException {
		System.setIn(new ByteArrayInputStream(commands.getBytes()));
	}

	@Test
	public void testReadAndDelete() throws Exception {
		final String commands = 
			"r 9lines.txt\n" +
			"2d\n"
			;
		
		openCommandString(commands);
		LineEditor.main(new String[0]);
		assertEquals("Line 3", buffer.getLine(2));
	}

	@Test
	public void testReadAndSubst() throws Exception {
		final String commands = 
			"r 9lines.txt\n" +
			"2s/Line/Type/\n"
			;
		
		openCommandString(commands);
		LineEditor.main(new String[0]);
		assertEquals("Type 2", buffer.getLine(2));
	}

}
