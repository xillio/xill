package nl.xillio.xill.plugins.document.constructs;

import nl.xillio.udm.exceptions.DocumentNotFoundException;
import nl.xillio.xill.TestUtils;
import nl.xillio.xill.api.components.MetaExpression;
import nl.xillio.xill.api.errors.RobotRuntimeException;
import nl.xillio.xill.plugins.document.services.XillUDMService;
import org.testng.annotations.Test;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.testng.Assert.assertEquals;

/**
 * Test the parsing of input of the getVersions construct.
 *
 * @author Thomas Biesaart
 */
public class GetVersionsConstructTest extends TestUtils {

	/**
	 * Test the process method under normal circumstances.
	 */
	@Test
	public void testProcessNormal() {
		// Mock context
		String id = "This is a document id";
		String section = "target";
		List<String> versions = Arrays.asList("1", "1.2", "2");

		XillUDMService xillUDMService = mock(XillUDMService.class);
		when(xillUDMService.getVersions(eq(id), eq(section))).thenReturn(versions);

		// Call the process
		MetaExpression result = GetVersionsConstruct.process(fromValue(id), fromValue(section), xillUDMService);

		// Get result
		List<String> stringResult = ((List<?>)result.getValue()).stream().map(Object::toString).collect(Collectors.toList());
		assertEquals(stringResult, versions);
	}

	@Test(expectedExceptions = RobotRuntimeException.class)
	public void testProcessFailed() {
		// Mock context
		String id = "This is a document id";
		String section = "target";

		XillUDMService xillUDMService = mock(XillUDMService.class);
		when(xillUDMService.getVersions(eq(id), eq(section))).thenThrow(new DocumentNotFoundException(""));

		// Call the process
		GetVersionsConstruct.process(fromValue(id), fromValue(section), xillUDMService);
	}




}