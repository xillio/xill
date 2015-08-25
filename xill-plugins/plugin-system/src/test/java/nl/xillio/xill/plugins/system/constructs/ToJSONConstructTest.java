package nl.xillio.xill.plugins.system.constructs;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import nl.xillio.xill.api.components.MetaExpression;
import nl.xillio.xill.services.json.JsonParser;
import nl.xillio.xill.testutils.ConstructTest;

import org.testng.Assert;
import org.testng.annotations.Test;

/**
 * Test the {@link ToJSONConstruct}
 */
public class ToJSONConstructTest extends ConstructTest {

	/**
	 * Test the process method under normal circumstances
	 */
	@Test
	public void testProcess() {
		// Mock context
		String output = "This is the output";
		MetaExpression input = mockExpression(ATOMIC);
		JsonParser parser = mock(JsonParser.class);
		when(parser.toJson(input)).thenReturn(output);

		MetaExpression pretty = mockExpression(ATOMIC);
		when(pretty.getBooleanValue()).thenReturn(false);

		// Run
		MetaExpression result = ToJSONConstruct.process(input, pretty, parser, null);

		// Verify
		verify(parser).toJson(input);

		// Assert
		Assert.assertSame(result.getStringValue(), output);
	}

	/**
	 * Test the process method under normal circumstances with pretty printing
	 */
	@Test
	public void testProcessPretty() {
		// Mock context
		String output = "This is the output";
		MetaExpression input = mockExpression(ATOMIC);
		JsonParser parser = mock(JsonParser.class);
		when(parser.toJson(input)).thenReturn(output);

		MetaExpression pretty = mockExpression(ATOMIC);
		when(pretty.getBooleanValue()).thenReturn(true);

		// Run
		MetaExpression result = ToJSONConstruct.process(input, pretty, null, parser);

		// Verify
		verify(parser).toJson(input);

		// Assert
		Assert.assertSame(result.getStringValue(), output);
	}
}
