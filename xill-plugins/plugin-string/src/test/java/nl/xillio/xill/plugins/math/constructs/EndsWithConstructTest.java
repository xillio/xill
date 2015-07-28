package nl.xillio.xill.plugins.math.constructs;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import nl.xillio.xill.api.components.MetaExpression;
import nl.xillio.xill.api.construct.ExpressionBuilderHelper;
import nl.xillio.xill.plugins.string.constructs.EndsWithConstruct;
import nl.xillio.xill.plugins.string.services.string.StringService;

import org.testng.Assert;
import org.testng.annotations.Test;

/**
 * Test the {@link EndsWithConstruct}.
 */
public class EndsWithConstructTest extends ExpressionBuilderHelper {

	/**
	 * Test the process method under normal circumstances.
	 */
	@Test
	public void processListUsage() {
		// Mock
		String parentValue = "testing";
		MetaExpression parent = mock(MetaExpression.class);
		when(parent.getStringValue()).thenReturn(parentValue);

		String childValue = "ing";
		MetaExpression child = mock(MetaExpression.class);
		when(child.getStringValue()).thenReturn(childValue);

		boolean returnValue = true;
		StringService stringService = mock(StringService.class);
		when(stringService.endsWith(parentValue, childValue)).thenReturn(returnValue);
		// Run
		MetaExpression result = EndsWithConstruct.process(parent, child, stringService);

		// Verify
		verify(stringService, times(1)).endsWith(parentValue, childValue);

		// Assert
		Assert.assertEquals(result.getBooleanValue(), returnValue);
	}
}
