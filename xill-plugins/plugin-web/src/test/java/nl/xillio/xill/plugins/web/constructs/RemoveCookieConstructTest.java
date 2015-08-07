package nl.xillio.xill.plugins.web.constructs;

import static org.mockito.Mockito.*;

import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Set;

import nl.xillio.xill.api.components.MetaExpression;
import nl.xillio.xill.api.construct.ExpressionBuilderHelper;
import nl.xillio.xill.api.errors.RobotRuntimeException;
import nl.xillio.xill.plugins.web.NodeVariable;
import nl.xillio.xill.plugins.web.PageVariable;
import nl.xillio.xill.plugins.web.PhantomJSPool;
import nl.xillio.xill.plugins.web.services.web.WebService;

import org.openqa.selenium.Cookie;
import org.testng.Assert;
import org.testng.annotations.Test;

import com.google.common.collect.Sets;


/**
 *
 */
public class RemoveCookieConstructTest extends ExpressionBuilderHelper {
	
	@Test
	public void testProcessListUsage(){
		// mock
		WebService webService = mock(WebService.class);
		
		//The values the input contains
		MetaExpression first = mock(MetaExpression.class);
		MetaExpression second = mock(MetaExpression.class);
		when(first.getStringValue()).thenReturn("first cookie!");
		when(second.getStringValue()).thenReturn("second cookie...");
		List<MetaExpression> inputValue = Arrays.asList(first, second);
		
		//The page
		PageVariable pageVariable = mock(PageVariable.class);
		MetaExpression page = mock(MetaExpression.class);
		when(page.getMeta(PageVariable.class)).thenReturn(pageVariable);
		
		//The cookie
		MetaExpression cookie = mock(MetaExpression.class);
		when(cookie.isNull()).thenReturn(false);
		when(cookie.getValue()).thenReturn(inputValue);
		when(cookie.getType()).thenReturn(LIST);

		// run
		MetaExpression output = RemoveCookieConstruct.process(page, cookie, webService);

		// verify
		
		//Wheter we ask for their values only once
		verify(first, times(1)).getStringValue();
		verify(second, times(1)).getStringValue();
		
		//Wheter we parse the pageVariable only once
		verify(page, times(2)).getMeta(PageVariable.class);
		
		//Wheter we parse the cookie only once
		verify(cookie, times(1)).isNull();
		verify(cookie, times(1)).getValue();
		verify(cookie, times(1)).getType();
		
		//We had two items to delete
		verify(webService, times(2)).deleteCookieNamed(eq(pageVariable), anyString());
		
		// assert
		Assert.assertEquals(output, NULL);
	}
	
	@Test
	public void testProcessAtomicUsage(){
		// mock
		WebService webService = mock(WebService.class);
		
		//The page
		PageVariable pageVariable = mock(PageVariable.class);
		MetaExpression page = mock(MetaExpression.class);
		when(page.getMeta(PageVariable.class)).thenReturn(pageVariable);
		
		//The cookie
		MetaExpression cookie = mock(MetaExpression.class);
		when(cookie.isNull()).thenReturn(false);
		when(cookie.getStringValue()).thenReturn("deleteThisCookie");
		when(cookie.getType()).thenReturn(ATOMIC);
		
		// run
		MetaExpression output = RemoveCookieConstruct.process(page, cookie, webService);
		
		//Wheter we parse the pageVariable only once
		verify(page, times(2)).getMeta(PageVariable.class);
		
		//Wheter we parse the cookie only once
		verify(cookie, times(1)).isNull();
		verify(cookie, times(1)).getStringValue();
		verify(cookie, times(1)).getType();
		
		//We had one item to delete
		verify(webService, times(1)).deleteCookieNamed(eq(pageVariable), anyString());
		
		// assert
		Assert.assertEquals(output, NULL);		
	}
	
	/**
	 * Test the process when the webService breaks
	 */
	@Test(expectedExceptions = RobotRuntimeException.class)
	public void testProcessWhenWebServiceBreaks(){
		// mock
		WebService webService = mock(WebService.class);
		
		//The page
		PageVariable pageVariable = mock(PageVariable.class);
		MetaExpression page = mock(MetaExpression.class);
		when(page.getMeta(PageVariable.class)).thenReturn(pageVariable);
		
		//The cookie
		MetaExpression cookie = mock(MetaExpression.class);
		when(cookie.isNull()).thenReturn(false);
		when(cookie.getValue()).thenReturn("deleteThisCookie");
		when(cookie.getType()).thenReturn(ATOMIC);
		
		//we make it break
		doThrow(new RobotRuntimeException("Resistance is futile.")).when(webService).deleteCookieNamed(any(), anyString());
		
		RemoveCookieConstruct.process(page, cookie, webService);
	}
	
	/**
	 * Test the process when no page is given.
	 */
	@Test(expectedExceptions = RobotRuntimeException.class, expectedExceptionsMessageRegExp = "Invalid variable type. Node PAGE type expected!")
	public void testProcessNoPageGiven(){
		// mock
		WebService webService = mock(WebService.class);
		
		//The page
		MetaExpression page = mock(MetaExpression.class);
		when(page.getMeta(PageVariable.class)).thenReturn(null);
		
		//The cookie
		MetaExpression cookie = mock(MetaExpression.class);
		
		// run
		RemoveCookieConstruct.process(page, cookie, webService);
	}
}
