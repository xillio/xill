package nl.xillio.xill.docgen.data;

import org.testng.annotations.Test;

import static org.testng.Assert.*;

/**
 * Test the code cleaner for ExampleNodeTest
 */
public class ExampleNodeTest {

	@Test
	public void testCodeCleaner() throws Exception {
		String code =
				"			Test Line		\n" +
				"			Other Line\n" +
				"				Indented Line\n" +
				"\n" +
				"			root again";
		String cleanCode =
				"Test Line\n" +
				"Other Line\n" +
				"	Indented Line\n" +
				"\n" +
				"root again";

		ExampleNode node = new ExampleNode("code", code);

		assertEquals(node.getContent(), cleanCode);
	}
}