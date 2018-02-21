package edj;

import static org.junit.Assert.*;

import java.util.Arrays;

import org.junit.Before;
import org.junit.Test;

public class LineEditorTest {
	
	AbstractBufferPrims target;

	@Before
	public void setUp() throws Exception {
		target = new BufferPrimsNoUndo();
	}

	// Parsing tests

	@Test
	public void testGetLineRange2() {
		int[] ret = target.getLineRange("3,6d");
		assertEquals(3, ret[0]);
		assertEquals(6, ret[1]);
		assertEquals('d', ret[2]);
	}
	
	@Test
	public void testGetLineRange1() {
		int[] ret = target.getLineRange("6d");
		assertEquals(6, ret[0]);
		assertEquals(BufferPrims.INF, ret[1]);
		assertEquals('d', ret[2]);
	}
	
	@Test
	public void testGetLineRangeCommaOnlyNoLines() {
		int[] ret = target.getLineRange(",p");
		assertEquals(BufferPrims.NO_NUM, ret[0]);
		assertEquals(BufferPrims.INF, ret[1]);
		assertEquals('p', ret[2]);
	}
	
	@Test
	public void testGetLineRangeCommaOnlySomeLines() {
		target.addLines(Arrays.asList(new String[]{"One","Two"}));
		int[] ret = target.getLineRange(",p");
		assertEquals(1, ret[0]);
		assertEquals(2, ret[1]);
		assertEquals('p', ret[2]);
	}
	
	@Test
	public void testGetLineRange0() {
		int[] ret = target.getLineRange("d");
		assertEquals(BufferPrims.NO_NUM, ret[0]);
		assertEquals(BufferPrims.INF, ret[1]);
		assertEquals('d', ret[2]);
	}
}
