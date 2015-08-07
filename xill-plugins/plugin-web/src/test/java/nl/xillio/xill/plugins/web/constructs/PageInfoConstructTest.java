package nl.xillio.xill.plugins.web.constructs;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.LinkedHashMap;

import nl.xillio.xill.api.components.MetaExpression;
import nl.xillio.xill.api.construct.ExpressionBuilderHelper;
import nl.xillio.xill.api.errors.RobotRuntimeException;
import nl.xillio.xill.plugins.web.NodeVariable;
import nl.xillio.xill.plugins.web.PageVariable;
import nl.xillio.xill.plugins.web.services.web.WebService;

import org.openqa.selenium.Cookie;
import org.testng.Assert;
import org.testng.annotations.Test;

import com.google.common.collect.Sets;

public class PageInfoConstructTest extends ExpressionBuilderHelper {

	/**
	 * Tests the construct under normal circumstances
	 */
	@Test
	public void testProcessNormalUsage() {
		// mock
		WebService webService = mock(WebService.class);

		// The page
		MetaExpression input = mock(MetaExpression.class);
		PageVariable pageVariable = mock(PageVariable.class);
		when(input.getMeta(PageVariable.class)).thenReturn(pageVariable);

		// The cookie extracted
		Cookie first = mock(Cookie.class);
		when(webService.getName(first)).thenReturn("first");
		when(webService.getDomain(first)).thenReturn("first domain");
		when(webService.getPath(first)).thenReturn("first path");
		when(webService.getValue(first)).thenReturn("first value");

		// the process
		when(webService.getCookies(pageVariable)).thenReturn(Sets.newHashSet(first));

		// runs
		MetaExpression output = PageInfoConstruct.process(input, webService);

		// verify
		verify(input, times(2)).getMeta(PageVariable.class);
		verify(webService, times(2)).getName(first);
		verify(webService, times(1)).getDomain(first);
		verify(webService, times(1)).getPath(first);
		verify(webService, times(1)).getValue(first);

		// assert
		Assert.assertEquals(output.getType(), OBJECT);
		@SuppressWarnings("unchecked")
		LinkedHashMap<String, MetaExpression> result = (LinkedHashMap<String, MetaExpression>) output.getValue();
		Assert.assertEquals(result.getOrDefault("cookies", NULL).getType(), OBJECT);
	}

	/**
	 * Test the process when no node was in the expression.
	 */
	@Test(expectedExceptions = RobotRuntimeException.class, expectedExceptionsMessageRegExp = "Invalid variable type. Node PAGE type expected!")
	public void testProcessNoNodeGiven() {
		// mock
		WebService webService = mock(WebService.class);

		// The input
		MetaExpression input = mock(MetaExpression.class);

		when(input.getMeta(NodeVariable.class)).thenReturn(null);

		// run
		PageInfoConstruct.process(input, webService);
	}

	/**
	 * Test the process when selenium breaks.
	 */
	@Test(expectedExceptions = RobotRuntimeException.class)
	public void testProcessWhenItBreaks() {
		// mock
		WebService webService = mock(WebService.class);

		// The input
		MetaExpression input = mock(MetaExpression.class);
		PageVariable pageVariable = mock(PageVariable.class);
		when(input.getMeta(PageVariable.class)).thenReturn(pageVariable);

		// The cookie extracted
		Cookie first = mock(Cookie.class);
		when(webService.getName(first)).thenThrow(new RobotRuntimeException("We throw"));
		when(webService.getDomain(first)).thenReturn("first domain");
		when(webService.getPath(first)).thenReturn("first path");
		when(webService.getValue(first)).thenReturn("first value");

		// the process
		when(webService.getCookies(pageVariable)).thenReturn(Sets.newHashSet(first));

		// runs
		PageInfoConstruct.process(input, webService);
	}

}
