package nl.xillio.xill.plugins.web.constructs;

import static org.mockito.Matchers.any;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.HashMap;
import java.util.Map;

import nl.xillio.xill.api.components.MetaExpression;
import nl.xillio.xill.api.construct.ExpressionBuilderHelper;
import nl.xillio.xill.api.errors.RobotRuntimeException;
import nl.xillio.xill.plugins.web.data.CookieFactory;
import nl.xillio.xill.plugins.web.data.NodeVariable;
import nl.xillio.xill.plugins.web.data.WebVariable;
import nl.xillio.xill.plugins.web.services.web.WebService;

import org.openqa.selenium.StaleElementReferenceException;
import org.testng.Assert;
import org.testng.annotations.Test;

public class CookieFactoryTest {
	
	/**
	 * Test the construct when expected values are given.
	 */
	@Test
	public void testProcessNormalUsage(){
		// mock
		WebVariable webVariable = mock(WebVariable.class);
		WebService webService = mock(WebService.class);

		Map<String, MetaExpression> cookie = new HashMap<>();
		cookie.put("name", getName("A name"));
		cookie.put("domain", getDomain("A domain"));
		cookie.put("path", getPath("A path"));
		cookie.put("value", getValue("A value"));
		cookie.put("expires", getExpires("1995-02-03T12:12:12"));
		CookieFactory factory = new CookieFactory();
		
		//run
		factory.setCookie(webVariable, cookie, webService);
	}
	
	/**
	 * Test the construct when expected values are given, but the metaExpressions in the cookiesettings evalueate to "null".
	 */
	@Test
	public void testProcessNullStringsInInput(){
		// mock
		WebVariable webVariable = mock(WebVariable.class);
		WebService webService = mock(WebService.class);

		Map<String, MetaExpression> cookie = new HashMap<>();
		cookie.put("name", getName("A name"));
		cookie.put("domain", getDomain("null"));
		cookie.put("path", getPath("null"));
		cookie.put("value", getValue("null"));
		cookie.put("expires", getExpires("null"));
		CookieFactory factory = new CookieFactory();
		
		//run
		factory.setCookie(webVariable, cookie, webService);
	}
	
	/**
	 * Test the construct when no values are given but the name value.
	 */
	@Test
	public void testProcessNoInputGivenButName(){
		// mock
		WebVariable webVariable = mock(WebVariable.class);
		WebService webService = mock(WebService.class);

		Map<String, MetaExpression> cookie = new HashMap<>();
		cookie.put("name", getName("A name"));
		CookieFactory factory = new CookieFactory();
		
		//run
		factory.setCookie(webVariable, cookie, webService);
	}
	
	/**
	 * Test the construct when expected values are given, but the metaExpressions in the cookiesettings evalueate to "null".
	 */
	@Test(expectedExceptions = RobotRuntimeException.class, expectedExceptionsMessageRegExp = "Invalid cookie. Atribute \\'expires\\' does not have the format yyyy-MM-ddThh:mm:ss")
	public void testInvalidDateFormatForExpirationDate(){
		// mock
		WebVariable webVariable = mock(WebVariable.class);
		WebService webService = mock(WebService.class);

		Map<String, MetaExpression> cookie = new HashMap<>();
		cookie.put("name", getName("A name"));
		cookie.put("expires", getExpires("Invalid date format"));
		CookieFactory factory = new CookieFactory();
		
		//run
		factory.setCookie(webVariable, cookie, webService);
	}
	
	/**
	 * Test the construct when no cookie input is given.
	 */
	@Test(expectedExceptions = RobotRuntimeException.class, expectedExceptionsMessageRegExp = "Invalid cookie. Attribute \\'name\\' is empty.")
	public void testProcessWithNoInput(){
		// mock
		WebVariable webVariable = mock(WebVariable.class);
		WebService webService = mock(WebService.class);

		Map<String, MetaExpression> cookie = new HashMap<>();
		CookieFactory factory = new CookieFactory();
		
		//run
		factory.setCookie(webVariable, cookie, webService);
	}
	
	
	private MetaExpression getName(String value){
		MetaExpression name = mock(MetaExpression.class);
		when(name.getStringValue()).thenReturn(value);
		return name;
	}
	
	private MetaExpression getDomain(String value){
		MetaExpression domain = mock(MetaExpression.class);
		when(domain.getStringValue()).thenReturn(value);
		return domain;
	}
	
	private MetaExpression getPath(String value){
		MetaExpression path = mock(MetaExpression.class);
		when(path.getStringValue()).thenReturn(value);
		return path;		
	}
	
	private MetaExpression getValue(String value){
		MetaExpression valueMeta = mock(MetaExpression.class);
		when(valueMeta.getStringValue()).thenReturn(value);
		return valueMeta;
	}
	
	private MetaExpression getExpires(String value){
		MetaExpression expires = mock(MetaExpression.class);
		when(expires.getStringValue()).thenReturn(value);
		return expires;		
	}

}
