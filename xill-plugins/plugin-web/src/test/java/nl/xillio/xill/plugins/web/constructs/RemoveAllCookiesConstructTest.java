package nl.xillio.xill.plugins.web.constructs;

import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import nl.xillio.xill.api.components.MetaExpression;
import nl.xillio.xill.api.construct.ExpressionBuilderHelper;
import nl.xillio.xill.api.errors.RobotRuntimeException;
import nl.xillio.xill.plugins.web.data.PageVariable;
import nl.xillio.xill.plugins.web.services.web.WebService;

import org.testng.Assert;
import org.testng.annotations.Test;

/**
 * Tests the {@link RemoveAllCookiesConstruct}.
 *
 */
public class RemoveAllCookiesConstructTest extends ExpressionBuilderHelper {

	/**
	 * test the process with normal usage.
	 *
	 * @throws Exception
	 */
	@Test
	public void testProcessNormalUsage() throws Exception {
		// mock
		WebService webService = mock(WebService.class);

		// The page
		PageVariable pageVariable = mock(PageVariable.class);
		MetaExpression page = mock(MetaExpression.class);
		when(page.getMeta(PageVariable.class)).thenReturn(pageVariable);

		// run
		MetaExpression output = RemoveAllCookiesConstruct.process(page, webService);

		// Wheter we parse the pageVariable only once
		verify(page, times(2)).getMeta(PageVariable.class);

		// We had one item to delete
		verify(webService, times(1)).deleteCookies(pageVariable);

		// assert
		Assert.assertEquals(output, NULL);
	}

	/**
	 * Test the process with null input given.
	 */
	@Test
	public void testNullInput() {
		// mock
		WebService webService = mock(WebService.class);
		MetaExpression input = mock(MetaExpression.class);
		when(input.isNull()).thenReturn(true);

		// run
		MetaExpression output = RemoveAllCookiesConstruct.process(input, webService);

		// assert
		Assert.assertEquals(output, NULL);
	}

	/**
	 * Test the process when the webService breaks
	 *
	 * @throws Exception
	 */
	@Test(expectedExceptions = RobotRuntimeException.class, expectedExceptionsMessageRegExp = "Failed to delete all cookies in driver.")
	public void testProcessWhenWebServiceBreaks() throws Exception {
		// mock
		WebService webService = mock(WebService.class);

		// The page
		PageVariable pageVariable = mock(PageVariable.class);
		MetaExpression page = mock(MetaExpression.class);
		when(page.getMeta(PageVariable.class)).thenReturn(pageVariable);

		// we make it break
		doThrow(new RobotRuntimeException("Resistance is futile.")).when(webService).deleteCookies(pageVariable);

		// run
		RemoveAllCookiesConstruct.process(page, webService);
	}

	/**
	 * Test the process when no page is given.
	 */
	@Test(expectedExceptions = RobotRuntimeException.class, expectedExceptionsMessageRegExp = "Invalid variable type. Node PAGE type expected!")
	public void testProcessNoPageGiven() {
		// mock
		WebService webService = mock(WebService.class);

		// The page
		MetaExpression page = mock(MetaExpression.class);
		when(page.getMeta(PageVariable.class)).thenReturn(null);

		// run
		RemoveAllCookiesConstruct.process(page, webService);
	}

}
