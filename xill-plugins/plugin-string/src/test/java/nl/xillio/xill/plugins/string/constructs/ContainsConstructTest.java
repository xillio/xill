package nl.xillio.xill.plugins.string.constructs;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import nl.xillio.xill.api.components.MetaExpression;
import nl.xillio.xill.plugins.string.services.string.StringService;

import org.testng.Assert;
import org.testng.annotations.Test;

/**
 * Test the {@link ContainsConstruct}.
 */
public class ContainsConstructTest {

	/**
	 * Test the process method under normal circumstances.
	 */
	@Test
	public void processNormalUsage() {
		// Mock
		String parentValue = "testing";
		MetaExpression parent = mock(MetaExpression.class);
		when(parent.getStringValue()).thenReturn(parentValue);

		String childValue = "ing";
		MetaExpression child = mock(MetaExpression.class);
		when(child.getStringValue()).thenReturn(childValue);

		boolean returnValue = true;
		StringService stringService = mock(StringService.class);
		when(stringService.contains(parentValue, childValue)).thenReturn(returnValue);
		// Run
		MetaExpression result = ContainsConstruct.process(parent, child, stringService);

		// Verify
		verify(stringService, times(1)).contains(parentValue, childValue);

		// Assert
		Assert.assertEquals(result.getBooleanValue(), returnValue);
	}
}
