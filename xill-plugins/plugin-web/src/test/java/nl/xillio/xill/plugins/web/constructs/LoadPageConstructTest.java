package nl.xillio.xill.plugins.web.constructs;

import static org.mockito.Matchers.any;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.LinkedHashMap;

import nl.xillio.xill.api.components.MetaExpression;
import nl.xillio.xill.api.construct.ExpressionBuilderHelper;
import nl.xillio.xill.api.errors.RobotRuntimeException;
import nl.xillio.xill.plugins.web.NodeVariable;
import nl.xillio.xill.plugins.web.services.web.WebService;

import org.openqa.selenium.StaleElementReferenceException;
import org.testng.Assert;
import org.testng.annotations.Test;

public class LoadPageConstructTest {

	
	@Test
	public void testNormalUsage(){
		
		// mock
		WebService webService = mock(WebService.class);
		
		//The url
		String urlValue = "This is an url";
		MetaExpression url = mock(MetaExpression.class);
		when(url.getStringValue()).thenReturn(urlValue);
		
		//The options
		LinkedHashMap<String, MetaExpression> optionsValue = new LinkedHashMap<>();
		
		
		//A big block for the proxyhost option:
		//------------------------------------------------------------------------------------------------------------------
		//The proxyHost:
		String proxyHostValue = "option of proxyhost";
		MetaExpression proxyHost = mock(MetaExpression.class);
		when(proxyHost.getStringValue()).thenReturn(proxyHostValue);
		optionsValue.put("proxyhost", proxyHost);
		
		//The proxy port, needed to make proxyhost run
		int proxyPortValue = 5;
		MetaExpression proxyPort = mock(MetaExpression.class);
		when(proxyPort.getNumberValue()).thenReturn(proxyPortValue);
		optionsValue.put("proxyport", proxyPort);
		
		//The proxy user
		String proxyUserValue = "value of the proxyUser";
		MetaExpression proxyUser = mock(MetaExpression.class);
		when(proxyUser.getStringValue()).thenReturn(proxyUserValue);
		optionsValue.put("proxyuser", proxyUser);
		
		//the proxy pass
		String proxyPassValue = "you shall pass";
		MetaExpression proxyPass = mock(MetaExpression.class);
		when(proxyPass.getStringValue()).thenReturn(proxyPassValue);
		optionsValue.put("proxypass", proxyPass);
		
		//the proxy type
		String proxyTypeValue = "socks5";
		MetaExpression proxyType = mock(MetaExpression.class);
		when(proxyType.getStringValue()).thenReturn(proxyTypeValue);
		optionsValue.put("proxytype", proxyType);
		//--------------------------------------------------------------------------------------------------------------------
		
		//All the other options:
		//-----------------------------------------------------------------------------------------------------------------
		//the enableJs option
		boolean enableJSValue = true;
		MetaExpression enableJS = mock(MetaExpression.class);
		when(enableJS.getBooleanValue()).thenReturn(enableJSValue);
		optionsValue.put("enablejs", enableJS);
		
		//the enableWebSecurity option
		boolean enableWebSecurityValue = true;
		MetaExpression enableWebSecurity = mock(MetaExpression.class);
		when(enableWebSecurity.getBooleanValue()).thenReturn(enableWebSecurityValue);
		optionsValue.put("enablewebsecurity", enableWebSecurity);
		
		//the loadImages option
		boolean loadImagesValue = true;
		MetaExpression loadImages = mock(MetaExpression.class);
		when(loadImages.getBooleanValue()).thenReturn(loadImagesValue);
		optionsValue.put("loadimages", loadImages);
		
		//the insecureSSL option
		boolean insecureSSLValue = true;
		MetaExpression insecureSSL = mock(MetaExpression.class);
		when(insecureSSL.getBooleanValue()).thenReturn(insecureSSLValue);
		optionsValue.put("insecuressl", insecureSSL);
		
		//the timeout option
		int timeoutValue = 5;
		MetaExpression timeout = mock(MetaExpression.class);
		when(timeout.getNumberValue()).thenReturn(timeoutValue);
		optionsValue.put("timeout", timeout);
		
		//the ltrUrlAccess option
		boolean ltrUrlAccessValue = true;
		MetaExpression ltrUrlAccess = mock(MetaExpression.class);
		when(ltrUrlAccess.getBooleanValue()).thenReturn(ltrUrlAccessValue);
		optionsValue.put("ltrurlaccess", ltrUrlAccess);
		
		//the sslProtocol option
		String sslProtocolValue = "sslv3";
		MetaExpression sslProtocol = mock(MetaExpression.class);
		when(sslProtocol.getStringValue()).thenReturn(sslProtocolValue);
		optionsValue.put("sslProtocol", sslProtocol);
		
		//the browser option
		String browserValue = "PHANTOMJS";
		MetaExpression browser = mock(MetaExpression.class);
		when(browser.getStringValue()).thenReturn(browserValue);
		optionsValue.put("browser", browser);
		
		//the user option
		String userValue = "httpAuthUser";
		MetaExpression user = mock(MetaExpression.class);
		when(user.getStringValue()).thenReturn(userValue);
		optionsValue.put("user", user);
		
		//the pass that belongs with the user
		String passValue = "user may pass";
		MetaExpression pass = mock(MetaExpression.class);
		when(pass.getStringValue()).thenReturn(passValue);
		optionsValue.put("pass", pass);
		//--------------------------------------------------------------------------------------
		
		
		// verify
		
		//The proxyhost option:
		verify(proxyHost, times(1)).getStringValue();
		verify(proxyPort, times(1)).getNumberValue();
		verify(proxyUser, times(1)).getStringValue();
		verify(proxyPass, times(1)).getStringValue();
		verify(proxyType, times(1)).getStringValue();
		
		//the other options:
		verify(enableJS, times(1)).getBooleanValue();
		verify(enableWebSecurity, times(1)).getBooleanValue();
		verify(loadImages, times(1)).getBooleanValue();
		verify(insecureSSL, times(1)).getBooleanValue();
		verify(timeout, times(1)).getNumberValue();
		verify(ltrUrlAccess, times(1)).getBooleanValue();
		verify(browser, times(1)).getStringValue();
		
	}

}
