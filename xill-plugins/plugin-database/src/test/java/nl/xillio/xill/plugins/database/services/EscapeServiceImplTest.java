package nl.xillio.xill.plugins.database.services;

import org.testng.annotations.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import static org.testng.Assert.*;

/**
 * Unit tests for the EscapeService implementation
 * @author Daan Knoope
 */
public class EscapeServiceImplTest {

	@Test
	public void testEscape() throws Exception {
		EscapeService service = new EscapeServiceImpl();

		List<String> basicInput = Arrays.asList("\\\\", "\\n", "\\r", "\\t", "\\b", "\\f", "\\0", "\\'", "\\\"");
		List<String> basicResults = Arrays.asList("\\\\\\\\", "\\\\n", "\\\\r", "\\\\t", "\\\\b", "\\\\f", "\\\\0", "\\\\'", "\\\\\"");
		List<String> output = basicInput.stream().map(service::escape).collect(Collectors.toList());
		assertEquals(output,basicResults);

		String sampleQuery = "\\\'teststring\\\'";
		String resultSampleQuery = "\\\\\'teststring\\\\\'";
		String outputSampleQuery = service.escape(sampleQuery);
		assertEquals(outputSampleQuery,resultSampleQuery);
	}
}
