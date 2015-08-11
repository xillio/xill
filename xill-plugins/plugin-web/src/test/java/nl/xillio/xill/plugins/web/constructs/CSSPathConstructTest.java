package nl.xillio.xill.plugins.web.constructs;

import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyString;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Arrays;
import java.util.List;

import nl.xillio.xill.api.components.MetaExpression;
import nl.xillio.xill.api.construct.ExpressionBuilderHelper;
import nl.xillio.xill.api.errors.RobotRuntimeException;
import nl.xillio.xill.plugins.web.data.NodeVariable;
import nl.xillio.xill.plugins.web.services.web.WebService;

import org.openqa.selenium.InvalidElementStateException;
import org.openqa.selenium.InvalidSelectorException;
import org.testng.Assert;
import org.testng.annotations.Test;

/**
 * Tests the {@link CSSPathConstruct}.
 *
 */
public class CSSPathConstructTest extends ExpressionBuilderHelper {

	/**
	 * test the construct with normal input.
	 */
	@Test
	public void testNormalNodeUsage() {
		// mock
		WebService webService = mock(WebService.class);

		// The element
		MetaExpression element = mock(MetaExpression.class);
		NodeVariable nodeVariable = mock(NodeVariable.class);
		when(element.getMeta(NodeVariable.class)).thenReturn(nodeVariable);

		// The css path
		String query = "cssPath";
		MetaExpression cssPath = mock(MetaExpression.class);
		when(cssPath.getStringValue()).thenReturn(query);

		// The result from findElements
		NodeVariable firstResult = mock(NodeVariable.class);
		NodeVariable secondResult = mock(NodeVariable.class);

		// The process
		when(webService.findElementsWithCssPath(nodeVariable, query)).thenReturn(Arrays.asList(firstResult, secondResult));
		when(webService.getAttribute(eq(firstResult), anyString())).thenReturn("first");
		when(webService.getAttribute(eq(secondResult), anyString())).thenReturn("second");
		when(webService.createNodeVariable(nodeVariable, firstResult)).thenReturn(firstResult);
		when(webService.createNodeVariable(nodeVariable, secondResult)).thenReturn(secondResult);

		// run
		MetaExpression output = CSSPathConstruct.process(element, cssPath, webService);

		// verify
		verify(cssPath, times(1)).getStringValue();
		verify(element, times(2)).getMeta(NodeVariable.class);
		verify(webService, times(1)).findElementsWithCssPath(nodeVariable, query);
		verify(webService, times(1)).getAttribute(eq(firstResult), anyString());
		verify(webService, times(1)).getAttribute(eq(secondResult), anyString());

		// assert
		Assert.assertEquals(output.getType(), LIST);
		@SuppressWarnings("unchecked")
		List<MetaExpression> result = (List<MetaExpression>) output.getValue();
		Assert.assertEquals(result.size(), 2);
	}

	/**
	 * test the construct when a single resultvalue is returned.
	 */
	@Test
	public void testProcessSingleResultValue() {
		// mock
		WebService webService = mock(WebService.class);

		// The element
		MetaExpression element = mock(MetaExpression.class);
		NodeVariable nodeVariable = mock(NodeVariable.class);
		when(element.getMeta(NodeVariable.class)).thenReturn(nodeVariable);

		// The css path
		String query = "cssPath";
		MetaExpression cssPath = mock(MetaExpression.class);
		when(cssPath.getStringValue()).thenReturn(query);

		// The result of findElements
		NodeVariable firstResult = mock(NodeVariable.class);

		// The process
		when(webService.findElementsWithCssPath(nodeVariable, query)).thenReturn(Arrays.asList(firstResult));
		when(webService.getAttribute(eq(firstResult), anyString())).thenReturn("first");
		when(webService.createNodeVariable(nodeVariable, firstResult)).thenReturn(firstResult);

		// run
		MetaExpression output = CSSPathConstruct.process(element, cssPath, webService);

		// verify
		verify(cssPath, times(1)).getStringValue();
		verify(element, times(2)).getMeta(NodeVariable.class);
		verify(webService, times(1)).findElementsWithCssPath(nodeVariable, query);
		verify(webService, times(1)).getAttribute(eq(firstResult), anyString());

		// assert
		Assert.assertEquals(output.getType(), ATOMIC);
	}

	/**
	 * test the process when no result can be found.
	 */
	@Test
	public void testProcessNoValueFound() {
		// mock
		WebService webService = mock(WebService.class);

		// The element
		MetaExpression element = mock(MetaExpression.class);
		NodeVariable nodeVariable = mock(NodeVariable.class);
		when(element.getMeta(NodeVariable.class)).thenReturn(nodeVariable);

		// The css path
		String query = "cssPath";
		MetaExpression cssPath = mock(MetaExpression.class);
		when(cssPath.getStringValue()).thenReturn(query);

		// The process
		when(webService.findElementsWithCssPath(nodeVariable, query)).thenReturn(Arrays.asList());

		// run
		MetaExpression output = CSSPathConstruct.process(element, cssPath, webService);

		// verify
		verify(cssPath, times(1)).getStringValue();
		verify(element, times(2)).getMeta(NodeVariable.class);
		verify(webService, times(1)).findElementsWithCssPath(nodeVariable, query);
		verify(webService, times(0)).getAttribute(any(), anyString());

		// assert
		Assert.assertEquals(output, NULL);
	}

	/**
	 * Test the construct when no node or page is given.
	 */
	@Test(expectedExceptions = RobotRuntimeException.class, expectedExceptionsMessageRegExp = "Invalid variable type. PAGE or NODE type expected!")
	public void testNoNodeOrPageGiven() {
		// mock
		WebService webService = mock(WebService.class);

		// The element
		MetaExpression element = mock(MetaExpression.class);
		NodeVariable nodeVariable = mock(NodeVariable.class);

		// The css path
		String query = "cssPath";
		MetaExpression cssPath = mock(MetaExpression.class);
		when(cssPath.getStringValue()).thenReturn(query);

		// The process
		when(webService.findElementsWithCssPath(nodeVariable, query)).thenReturn(Arrays.asList());

		// run
		CSSPathConstruct.process(element, cssPath, webService);
	}

	/**
	 * test the construct when a single resultvalue is returned.
	 */
	@Test(expectedExceptions = RobotRuntimeException.class, expectedExceptionsMessageRegExp = "Invalid CSSPath")
	public void testProcessInvalidSelectorExceptionThrown() {
		// mock
		WebService webService = mock(WebService.class);

		// The element
		MetaExpression element = mock(MetaExpression.class);
		NodeVariable nodeVariable = mock(NodeVariable.class);
		when(element.getMeta(NodeVariable.class)).thenReturn(nodeVariable);

		// The css path
		String query = "cssPath";
		MetaExpression cssPath = mock(MetaExpression.class);
		when(cssPath.getStringValue()).thenReturn(query);

		// The process
		when(webService.findElementsWithCssPath(nodeVariable, query)).thenThrow(new InvalidSelectorException("invalid selector"));

		// run
		CSSPathConstruct.process(element, cssPath, webService);
	}

	/**
	 * test the construct when a single resultvalue is returned.
	 */
	@Test(expectedExceptions = RobotRuntimeException.class, expectedExceptionsMessageRegExp = "Invalid CSSPath")
	public void testProcessInvaliElementStateExceptionThrown() {
		// mock
		WebService webService = mock(WebService.class);

		// The element
		MetaExpression element = mock(MetaExpression.class);
		NodeVariable nodeVariable = mock(NodeVariable.class);
		when(element.getMeta(NodeVariable.class)).thenReturn(nodeVariable);

		// The css path
		String query = "cssPath";
		MetaExpression cssPath = mock(MetaExpression.class);
		when(cssPath.getStringValue()).thenReturn(query);

		// The process
		when(webService.findElementsWithCssPath(nodeVariable, query)).thenThrow(new InvalidElementStateException());

		// run
		CSSPathConstruct.process(element, cssPath, webService);
	}
}
