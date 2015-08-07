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
 * Tests the {@link RemoveAllCookiesConstruct}.
 *
 */
public class RemoveAllCookiesConstructTest extends ExpressionBuilderHelper {
	
	
	/**
	 * test the process with normal usage.
	 */
	@Test
	public void testProcessNormalUsage(){
		// mock
		WebService webService = mock(WebService.class);
		
		//The page
		PageVariable pageVariable = mock(PageVariable.class);
		MetaExpression page = mock(MetaExpression.class);
		when(page.getMeta(PageVariable.class)).thenReturn(pageVariable);
		
		// run
		MetaExpression output = RemoveAllCookiesConstruct.process(page, webService);
		
		//Wheter we parse the pageVariable only once
		verify(page, times(2)).getMeta(PageVariable.class);
		
		//We had one item to delete
		verify(webService, times(1)).deleteCookies(pageVariable);
		
		// assert
		Assert.assertEquals(output, NULL);		
	}
	
	/**
	 * Test the process when the webService breaks
	 */
	@Test(expectedExceptions = RobotRuntimeException.class, expectedExceptionsMessageRegExp = "Failed to delete all cookies in driver.")
	public void testProcessWhenWebServiceBreaks(){
		// mock
		WebService webService = mock(WebService.class);
		
		//The page
		PageVariable pageVariable = mock(PageVariable.class);
		MetaExpression page = mock(MetaExpression.class);
		when(page.getMeta(PageVariable.class)).thenReturn(pageVariable);
		
		//we make it break
		doThrow(new RobotRuntimeException("Resistance is futile.")).when(webService).deleteCookies(pageVariable);
		
		// run
		RemoveAllCookiesConstruct.process(page, webService);
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
		when(page.getMeta(PhantomJSPool.Entity.class)).thenReturn(null);
		
		// run
		RemoveAllCookiesConstruct.process(page, webService);
	}

}
