package nl.xillio.xill.plugins.system.constructs;

import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyString;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.ArrayList;

import org.testng.Assert;
import org.testng.annotations.Test;

import com.google.gson.JsonSyntaxException;

import nl.xillio.xill.ConstructTest;
import nl.xillio.xill.api.components.MetaExpression;
import nl.xillio.xill.api.errors.RobotRuntimeException;
import nl.xillio.xill.services.json.JsonParser;

/**
 * Test the {@link ParseJSONConstruct}
 */
public class ParseJSONConstructTest extends ConstructTest {

	/**
	 * Test the process method under normal circumstances
	 */
	@Test
	public void testProcess() {
		// Mock context
		String json = "[\"this\", \"is\", \"valid\", \"json\"]";
		ArrayList<?> expectedResult = new ArrayList<>();
		MetaExpression expression = mockExpression(ATOMIC);
		when(expression.getStringValue()).thenReturn(json);
		JsonParser parser = mock(JsonParser.class);
		when(parser.fromJson(eq(json), any())).thenReturn(expectedResult);

		// Run method
		MetaExpression result = ParseJSONConstruct.process(expression, parser);

		// Verify calls to service
		verify(parser).fromJson(eq(json), any());

		// Assertions
		Assert.assertEquals(result.getValue(), expectedResult);

	}

	/**
	 * Test the process method with null input
	 */
	@Test(expectedExceptions = RobotRuntimeException.class)
	public void testProcessNullInput() {
		// Run method
		ParseJSONConstruct.process(NULL, null);
	}

	/**
	 * Test the process method with invalid json
	 *
	 * @throws Throwable
	 *         while testing
	 */
	@Test(expectedExceptions = RobotRuntimeException.class, expectedExceptionsMessageRegExp = "Invalid JSON input: (.+)")
	public void testProcessInvalid() throws Throwable {
		// Mock context
		MetaExpression expression = mockExpression(ATOMIC);
		JsonParser parser = mock(JsonParser.class);
		when(parser.fromJson(anyString(), any())).thenThrow(new JsonSyntaxException("CORRECT"));

		// Run method
		ParseJSONConstruct.process(expression, parser);
	}
}
