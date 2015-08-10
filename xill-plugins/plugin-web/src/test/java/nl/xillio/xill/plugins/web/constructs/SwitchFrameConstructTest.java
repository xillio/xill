package nl.xillio.xill.plugins.web.constructs;

import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyInt;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import nl.xillio.xill.api.components.MetaExpression;
import nl.xillio.xill.api.construct.ExpressionBuilderHelper;
import nl.xillio.xill.api.errors.RobotRuntimeException;
import nl.xillio.xill.plugins.web.NodeVariable;
import nl.xillio.xill.plugins.web.PageVariable;
import nl.xillio.xill.plugins.web.services.web.WebService;

import org.openqa.selenium.NoSuchFrameException;
import org.testng.Assert;
import org.testng.annotations.Test;

/**
 * Test the {@link SwithToFrameConstruct}.
 */
public class SwitchFrameConstructTest extends ExpressionBuilderHelper {

	/**
	 * Test the process under normal circumstances with a webelement given.
	 */
	@Test
	public void testProcessWithWebElement() {
		// mock
		WebService webService = mock(WebService.class);

		// the page
		MetaExpression page = mock(MetaExpression.class);
		PageVariable pageVariable = mock(PageVariable.class);
		when(page.getMeta(PageVariable.class)).thenReturn(pageVariable);

		// The element
		NodeVariable nodeVariable = mock(NodeVariable.class);
		MetaExpression node = mock(MetaExpression.class);
		when(node.getMeta(NodeVariable.class)).thenReturn(nodeVariable);

		// run
		MetaExpression output = SwitchFrameConstruct.process(page, node, webService);

		// verify
		verify(page, times(2)).getMeta(PageVariable.class);
		verify(node, times(2)).getMeta(NodeVariable.class);
		verify(webService, times(1)).switchToFrame(pageVariable, nodeVariable);

		// assert
		Assert.assertEquals(output, NULL);
	}

	/**
	 * Test the process under normal circumstances with an integer given.
	 */
	@Test
	public void testProcessWithInteger() {
		// mock
		WebService webService = mock(WebService.class);

		// the page
		MetaExpression page = mock(MetaExpression.class);
		PageVariable pageVariable = mock(PageVariable.class);
		when(page.getMeta(PageVariable.class)).thenReturn(pageVariable);

		// The frame
		int frameValue = 42;
		MetaExpression frame = mock(MetaExpression.class);
		when(frame.getValue()).thenReturn(frameValue);
		when(frame.getMeta(NodeVariable.class)).thenReturn(null);
		when(frame.getType()).thenReturn(ATOMIC);
		when(frame.getNumberValue()).thenReturn(frameValue);

		// run
		MetaExpression output = SwitchFrameConstruct.process(page, frame, webService);

		// verify
		verify(page, times(2)).getMeta(PageVariable.class);
		verify(frame, times(1)).getValue();
		verify(webService, times(1)).switchToFrame(pageVariable, frameValue);

		// assert
		Assert.assertEquals(output, NULL);
	}

	/**
	 * Test the process under normal circumstances with a String given.
	 */
	@Test
	public void testProcessWithString() {
		// mock
		WebService webService = mock(WebService.class);

		// the page
		MetaExpression page = mock(MetaExpression.class);
		PageVariable pageVariable = mock(PageVariable.class);
		when(page.getMeta(PageVariable.class)).thenReturn(pageVariable);

		// The frame
		String frameValue = "frame as a String";
		MetaExpression frame = mock(MetaExpression.class);
		when(frame.getValue()).thenReturn(frameValue);
		when(frame.getMeta(NodeVariable.class)).thenReturn(null);
		when(frame.getType()).thenReturn(ATOMIC);
		when(frame.getStringValue()).thenReturn(frameValue);
		when(frame.getNumberValue()).thenReturn(Double.NaN);

		// run
		MetaExpression output = SwitchFrameConstruct.process(page, frame, webService);

		// verify
		verify(page, times(2)).getMeta(PageVariable.class);
		verify(frame, times(1)).getValue();
		verify(webService, times(1)).switchToFrame(pageVariable, frameValue);

		// assert
		Assert.assertEquals(output, NULL);
	}

	/**
	 * Test the process under normal circumstances with an invalid variable type given.
	 */
	@Test(expectedExceptions = RobotRuntimeException.class, expectedExceptionsMessageRegExp = "Invalid variable type of frame parameter!")
	public void testProcessInvalidVariableType() {
		// mock
		WebService webService = mock(WebService.class);

		// the page
		PageVariable pageVariable = mock(PageVariable.class);
		MetaExpression page = mock(MetaExpression.class);
		when(page.getMeta(PageVariable.class)).thenReturn(pageVariable);

		// The frame
		int frameValue = 42;
		MetaExpression frame = mock(MetaExpression.class);
		when(frame.getValue()).thenReturn(null);
		when(frame.isNull()).thenReturn(true);
		when(frame.getMeta(NodeVariable.class)).thenReturn(null);
		when(frame.getType()).thenReturn(ATOMIC);
		when(frame.getNumberValue()).thenReturn(frameValue);

		// run
		SwitchFrameConstruct.process(page, frame, webService);
	}

	/**
	 * Test the process when the webservice fails and throws a no such frame exception.
	 */
	@Test(expectedExceptions = RobotRuntimeException.class, expectedExceptionsMessageRegExp = "Requested frame doesn't exist.")
	public void testNoSuchFrameException() {
		// mock
		WebService webService = mock(WebService.class);

		// The page
		MetaExpression page = mock(MetaExpression.class);
		PageVariable pageVariable = mock(PageVariable.class);
		when(page.getMeta(PageVariable.class)).thenReturn(pageVariable);

		// The frame
		int frameValue = 404;
		MetaExpression frame = mock(MetaExpression.class);
		when(frame.getValue()).thenReturn(frameValue);
		when(frame.getMeta(NodeVariable.class)).thenReturn(null);
		when(frame.getType()).thenReturn(ATOMIC);
		when(frame.getNumberValue()).thenReturn(frameValue);

		// The exception
		doThrow(new NoSuchFrameException("error")).when(webService).switchToFrame(any(), anyInt());

		// run
		MetaExpression output = SwitchFrameConstruct.process(page, frame, webService);

		// verify
		verify(page, times(2)).getMeta(PageVariable.class);
		verify(frame, times(1)).getValue();
		verify(webService, times(1)).switchToFrame(pageVariable, frameValue);

		// assert
		Assert.assertEquals(output, NULL);
	}

	/**
	 * Test the process with an invalid page handed.
	 */
	@Test(expectedExceptions = RobotRuntimeException.class, expectedExceptionsMessageRegExp = "Invalid variable type. Page NODE type expected!")
	public void testNoPageGiven() {
		// mock
		WebService webService = mock(WebService.class);

		// the page
		MetaExpression page = mock(MetaExpression.class);
		MetaExpression frame = mock(MetaExpression.class);

		// run
		SwitchFrameConstruct.process(page, frame, webService);
	}
}
