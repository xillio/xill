package nl.xillio.xill.plugins.web.constructs;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import nl.xillio.xill.api.components.MetaExpression;
import nl.xillio.xill.api.errors.RobotRuntimeException;
import nl.xillio.xill.plugins.web.NodeVariable;
import nl.xillio.xill.plugins.web.services.web.WebService;

import org.testng.Assert;
import org.testng.annotations.Test;

/**
 *Test the {@link SelectedConstruct}.
 *
 */
public class SelectedConstructTest {

	/**
	 * Test the process under normal circumstances.
	 */
	@Test
	public void testProcessNormalUsage() {
		// mock
		WebService webService = mock(WebService.class);

		// The element
		NodeVariable nodeVariable = mock(NodeVariable.class);
		MetaExpression node = mock(MetaExpression.class);
		when(node.getMeta(NodeVariable.class)).thenReturn(nodeVariable);

		// the process
		when(webService.isSelected(nodeVariable)).thenReturn(true);

		// run
		MetaExpression output = SelectedConstruct.process(node, webService);

		// verify
		verify(node, times(2)).getMeta(NodeVariable.class);
		verify(webService, times(1)).isSelected(nodeVariable);

		// assert
		Assert.assertEquals(output.getBooleanValue(), true);
	}

	/**
	 * Test the process when the webService fails.
	 */
	@Test(expectedExceptions = RobotRuntimeException.class, expectedExceptionsMessageRegExp = "Failed to access NODE correctly")
	public void testProcessWebServiceFailure() {
		// mock
		WebService webService = mock(WebService.class);

		// The element
		NodeVariable nodeVariable = mock(NodeVariable.class);
		MetaExpression node = mock(MetaExpression.class);
		when(node.getMeta(NodeVariable.class)).thenReturn(nodeVariable);

		// the process
		when(webService.isSelected(nodeVariable)).thenThrow(new RobotRuntimeException("I broke"));

		// run
		SelectedConstruct.process(node, webService);
	}

	/**
	 * Test the process when the webService fails.
	 */
	@Test(expectedExceptions = RobotRuntimeException.class, expectedExceptionsMessageRegExp = "Invalid variable type. NODE type expected!")
	public void testProcessNoNodeGiven() {
		// mock
		WebService webService = mock(WebService.class);

		// The element
		MetaExpression node = mock(MetaExpression.class);
		when(node.getMeta(NodeVariable.class)).thenReturn(null);

		// run
		SelectedConstruct.process(node, webService);
	}
}
