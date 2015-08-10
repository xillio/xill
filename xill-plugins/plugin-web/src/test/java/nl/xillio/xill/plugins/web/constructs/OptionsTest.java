package nl.xillio.xill.plugins.web.constructs;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.LinkedHashMap;

import nl.xillio.xill.api.components.MetaExpression;
import nl.xillio.xill.api.construct.ExpressionBuilderHelper;
import nl.xillio.xill.api.errors.RobotRuntimeException;
import nl.xillio.xill.plugins.web.Options;
import nl.xillio.xill.plugins.web.OptionsFactory;

import org.testng.Assert;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

public class OptionsTest extends ExpressionBuilderHelper {

	@DataProvider(name = "booleanOptionTest")
	public static Object[][] booleanTestStrings() {
		Object[][] result = {
				{"enablejs", "enablejs"},
				{"enablewebsecurity", "enablewebsecurity"},
				{"loadimages", "loadimages"},
				{"insecuressl", "insecuressl"},
				{"ltrurlaccess", "ltrurlaccess"}};
		return result;
	}
	
	@DataProvider(name = "proxytypes")
	public static Object[][] proxyTypeTest(){
		Object [][] result = {
				{"proxytype", "http"},
				{"proxytype", "socks5"},
				{"proxytype", null}
		};
		return result;
	}
	
	@DataProvider(name = "invalidHttpUserPasses")
		public static Object[][] invalidPasses(){
			Object [][] result = {
					{"pass", null},
					{"pass", ""}
			};
			return result;
		}
	

	@DataProvider(name = "stringOptionTest")
	public static Object[][] stringTestStrings() {
		Object[][] result = {
				{"sslprotocol", "sslv3"},
				{"sslprotocol", "sslv2"},
				{"sslprotocol", "tlsv1"},
				{"sslprotocol", "any"},
				{"browser", "PHANTOMJS"}
		};
		return result;
	}

	@Test(dataProvider = "proxytypes")
	public void testProxyOption(String proxytypes, String type) throws Exception {
		// mock
		OptionsFactory optionsFactory = new OptionsFactory();

		// The url
		String urlValue = "This is an url";
		MetaExpression url = mock(MetaExpression.class);
		when(url.getStringValue()).thenReturn(urlValue);

		// The options
		LinkedHashMap<String, MetaExpression> optionsValue = new LinkedHashMap<>();
		MetaExpression options = mock(MetaExpression.class);
		when(options.getValue()).thenReturn(optionsValue);
		when(options.getType()).thenReturn(OBJECT);

		// The proxyHost:
		String proxyHostValue = "option of proxyhost";
		MetaExpression proxyHost = mock(MetaExpression.class);
		when(proxyHost.getStringValue()).thenReturn(proxyHostValue);
		optionsValue.put("proxyhost", proxyHost);

		// The proxy port, needed to make proxyhost run
		int proxyPortValue = 5;
		MetaExpression proxyPort = mock(MetaExpression.class);
		when(proxyPort.getNumberValue()).thenReturn(proxyPortValue);
		optionsValue.put("proxyport", proxyPort);

		// The proxy user
		String proxyUserValue = "value of the proxyUser";
		MetaExpression proxyUser = mock(MetaExpression.class);
		when(proxyUser.getStringValue()).thenReturn(proxyUserValue);
		optionsValue.put("proxyuser", proxyUser);

		// the proxy pass
		String proxyPassValue = "you shall pass";
		MetaExpression proxyPass = mock(MetaExpression.class);
		when(proxyPass.getStringValue()).thenReturn(proxyPassValue);
		optionsValue.put("proxypass", proxyPass);

		// the proxy type
		String proxyTypeValue = type;
		MetaExpression proxyType = mock(MetaExpression.class);
		when(proxyType.getStringValue()).thenReturn(proxyTypeValue);
		optionsValue.put(proxytypes, proxyType);

		// run
		Options output = optionsFactory.processOptions(options);

		// verify
		verify(proxyHost, times(1)).getStringValue();
		verify(proxyPort, times(1)).getNumberValue();
		verify(proxyUser, times(1)).getStringValue();
		verify(proxyPass, times(1)).getStringValue();
		verify(proxyType, times(1)).getStringValue();

		// assert
		Assert.assertEquals(output.getProxyUser(), proxyUserValue);
		Assert.assertEquals(output.getProxyPass(), proxyPassValue);
	}

	@Test(expectedExceptions = RobotRuntimeException.class, expectedExceptionsMessageRegExp = "Invalid proxytype.")
	public void TestProxyOptionInvalidProxyType() throws Exception {
		// mock
		OptionsFactory optionsFactory = new OptionsFactory();

		// The url
		String urlValue = "This is an url";
		MetaExpression url = mock(MetaExpression.class);
		when(url.getStringValue()).thenReturn(urlValue);

		// The options
		LinkedHashMap<String, MetaExpression> optionsValue = new LinkedHashMap<>();
		MetaExpression options = mock(MetaExpression.class);
		when(options.getValue()).thenReturn(optionsValue);
		when(options.getType()).thenReturn(OBJECT);

		// The proxyHost:
		String proxyHostValue = "option of proxyhost";
		MetaExpression proxyHost = mock(MetaExpression.class);
		when(proxyHost.getStringValue()).thenReturn(proxyHostValue);
		optionsValue.put("proxyhost", proxyHost);

		// The proxy port, needed to make proxyhost run
		int proxyPortValue = 5;
		MetaExpression proxyPort = mock(MetaExpression.class);
		when(proxyPort.getNumberValue()).thenReturn(proxyPortValue);
		optionsValue.put("proxyport", proxyPort);

		// The proxy user
		String proxyUserValue = "value of the proxyUser";
		MetaExpression proxyUser = mock(MetaExpression.class);
		when(proxyUser.getStringValue()).thenReturn(proxyUserValue);
		optionsValue.put("proxyuser", proxyUser);

		// the proxy pass
		String proxyPassValue = "you shall pass";
		MetaExpression proxyPass = mock(MetaExpression.class);
		when(proxyPass.getStringValue()).thenReturn(proxyPassValue);
		optionsValue.put("proxypass", proxyPass);

		// the proxy type
		String proxyTypeValue = "invalidValue";
		MetaExpression proxyType = mock(MetaExpression.class);
		when(proxyType.getStringValue()).thenReturn(proxyTypeValue);
		optionsValue.put("proxytype", proxyType);

		// run
		optionsFactory.processOptions(options);
	}
	
	@Test(expectedExceptions = RobotRuntimeException.class, expectedExceptionsMessageRegExp = "Proxyport must be given in the options OBJECT")
	public void TestProxyNoProxyPort() throws Exception {
		// mock
		OptionsFactory optionsFactory = new OptionsFactory();

		// The url
		String urlValue = "This is an url";
		MetaExpression url = mock(MetaExpression.class);
		when(url.getStringValue()).thenReturn(urlValue);

		// The options
		LinkedHashMap<String, MetaExpression> optionsValue = new LinkedHashMap<>();
		MetaExpression options = mock(MetaExpression.class);
		when(options.getValue()).thenReturn(optionsValue);
		when(options.getType()).thenReturn(OBJECT);

		// The proxyHost:
		String proxyHostValue = "option of proxyhost";
		MetaExpression proxyHost = mock(MetaExpression.class);
		when(proxyHost.getStringValue()).thenReturn(proxyHostValue);
		optionsValue.put("proxyhost", proxyHost);

		// The proxy user
		String proxyUserValue = "value of the proxyUser";
		MetaExpression proxyUser = mock(MetaExpression.class);
		when(proxyUser.getStringValue()).thenReturn(proxyUserValue);
		optionsValue.put("proxyuser", proxyUser);

		// the proxy pass
		String proxyPassValue = "you shall pass";
		MetaExpression proxyPass = mock(MetaExpression.class);
		when(proxyPass.getStringValue()).thenReturn(proxyPassValue);
		optionsValue.put("proxypass", proxyPass);

		// the proxy type
		String proxyTypeValue = "invalidValue";
		MetaExpression proxyType = mock(MetaExpression.class);
		when(proxyType.getStringValue()).thenReturn(proxyTypeValue);
		optionsValue.put("proxytype", proxyType);

		// run
		optionsFactory.processOptions(options);
	}

	@Test(expectedExceptions = RobotRuntimeException.class, expectedExceptionsMessageRegExp = "The Proxyuser and proxypass must either both be set up in the options OBJECT or none of them.")
	public void testProxyOptionIncorrectSetup() throws Exception {
		// mock
		OptionsFactory optionsFactory = new OptionsFactory();

		// The url
		String urlValue = "This is an url";
		MetaExpression url = mock(MetaExpression.class);
		when(url.getStringValue()).thenReturn(urlValue);

		// The options
		LinkedHashMap<String, MetaExpression> optionsValue = new LinkedHashMap<>();
		MetaExpression options = mock(MetaExpression.class);
		when(options.getValue()).thenReturn(optionsValue);
		when(options.getType()).thenReturn(OBJECT);

		// The proxyHost:
		String proxyHostValue = "option of proxyhost";
		MetaExpression proxyHost = mock(MetaExpression.class);
		when(proxyHost.getStringValue()).thenReturn(proxyHostValue);
		optionsValue.put("proxyhost", proxyHost);

		// The proxy port, needed to make proxyhost run
		int proxyPortValue = 5;
		MetaExpression proxyPort = mock(MetaExpression.class);
		when(proxyPort.getNumberValue()).thenReturn(proxyPortValue);
		optionsValue.put("proxyport", proxyPort);

		// The proxy user
		String proxyUserValue = "value of the proxyUser";
		MetaExpression proxyUser = mock(MetaExpression.class);
		when(proxyUser.getStringValue()).thenReturn(proxyUserValue);
		optionsValue.put("proxyuser", proxyUser);

		// the proxy pass
		String proxyPassValue = null;
		MetaExpression proxyPass = mock(MetaExpression.class);
		when(proxyPass.getStringValue()).thenReturn(proxyPassValue);
		optionsValue.put("proxypass", proxyPass);

		// the proxy type
		String proxyTypeValue = "socks5";
		MetaExpression proxyType = mock(MetaExpression.class);
		when(proxyType.getStringValue()).thenReturn(proxyTypeValue);
		optionsValue.put("proxytype", proxyType);

		// run
		optionsFactory.processOptions(options);
	}

	@Test
	public void testUserOption() throws Exception {
		// mock
		OptionsFactory optionsFactory = new OptionsFactory();

		// The url
		String urlValue = "This is an url";
		MetaExpression url = mock(MetaExpression.class);
		when(url.getStringValue()).thenReturn(urlValue);

		// The options
		LinkedHashMap<String, MetaExpression> optionsValue = new LinkedHashMap<>();
		MetaExpression options = mock(MetaExpression.class);
		when(options.getValue()).thenReturn(optionsValue);
		when(options.getType()).thenReturn(OBJECT);

		// the user option
		String userValue = "httpAuthUser";
		MetaExpression user = mock(MetaExpression.class);
		when(user.getStringValue()).thenReturn(userValue);
		optionsValue.put("user", user);

		// the pass that belongs with the user
		String passValue = "user may pass";
		MetaExpression pass = mock(MetaExpression.class);
		when(pass.getStringValue()).thenReturn(passValue);
		optionsValue.put("pass", pass);

		Options output = optionsFactory.processOptions(options);

		// verify
		verify(user, times(1)).getStringValue();
		verify(pass, times(1)).getStringValue();

		// assert
		Assert.assertEquals(output.getHttpAuthUser(), userValue);
		Assert.assertEquals(output.getHttpAuthPass(), passValue);
	}

	@Test(dataProvider = "invalidHttpUserPasses", expectedExceptions = RobotRuntimeException.class, expectedExceptionsMessageRegExp = "Http password must be set if user is used.")
	public void testUserOptionNoHTTPPass(String httpUserpass, String passValue) throws Exception {
		// mock
		OptionsFactory optionsFactory = new OptionsFactory();

		// The url
		String urlValue = "This is an url";
		MetaExpression url = mock(MetaExpression.class);
		when(url.getStringValue()).thenReturn(urlValue);

		// The options
		LinkedHashMap<String, MetaExpression> optionsValue = new LinkedHashMap<>();
		MetaExpression options = mock(MetaExpression.class);
		when(options.getValue()).thenReturn(optionsValue);
		when(options.getType()).thenReturn(OBJECT);

		// the user option
		String userValue = "httpAuthUser";
		MetaExpression user = mock(MetaExpression.class);
		when(user.getStringValue()).thenReturn(userValue);
		optionsValue.put("user", user);

		// the pass that belongs with the user
		MetaExpression pass = mock(MetaExpression.class);
		when(pass.getStringValue()).thenReturn(passValue);
		optionsValue.put(httpUserpass, pass);

		optionsFactory.processOptions(options);
	}

	@Test(dataProvider = "booleanOptionTest")
	public void testBooleanOptions(final String name, final String name2) throws Exception {
		// mock
		OptionsFactory optionsFactory = new OptionsFactory();

		// The url
		String urlValue = "This is an url";
		MetaExpression url = mock(MetaExpression.class);
		when(url.getStringValue()).thenReturn(urlValue);

		// The options
		LinkedHashMap<String, MetaExpression> optionsValue = new LinkedHashMap<>();
		boolean enable = true;
		MetaExpression option = mock(MetaExpression.class);
		when(option.getBooleanValue()).thenReturn(enable);
		optionsValue.put(name, option);

		MetaExpression options = mock(MetaExpression.class);
		when(options.getValue()).thenReturn(optionsValue);
		when(options.getType()).thenReturn(OBJECT);

		// run
		optionsFactory.processOptions(options);

		// verify
		verify(option, times(1)).getBooleanValue();
	}

	@Test(dataProvider = "stringOptionTest")
	public void testStringOptions(final String tagName, final String value) throws Exception {
		// mock
		OptionsFactory optionsFactory = new OptionsFactory();

		// The url
		String urlValue = "This is an url";
		MetaExpression url = mock(MetaExpression.class);
		when(url.getStringValue()).thenReturn(urlValue);

		// The options
		LinkedHashMap<String, MetaExpression> optionsValue = new LinkedHashMap<>();
		MetaExpression option = mock(MetaExpression.class);
		when(option.getStringValue()).thenReturn(value);
		optionsValue.put(tagName, option);

		MetaExpression options = mock(MetaExpression.class);
		when(options.getValue()).thenReturn(optionsValue);
		when(options.getType()).thenReturn(OBJECT);

		// run
		optionsFactory.processOptions(options);

		// verify
		verify(option, times(1)).getStringValue();
	}

	@Test(expectedExceptions = RobotRuntimeException.class, expectedExceptionsMessageRegExp = "Invalid sslprotocol.")
	public void testInvalidSslProtocol() throws Exception {
		// mock
		OptionsFactory optionsFactory = new OptionsFactory();

		// The url
		String urlValue = "This is an url";
		MetaExpression url = mock(MetaExpression.class);
		when(url.getStringValue()).thenReturn(urlValue);

		// The options
		LinkedHashMap<String, MetaExpression> optionsValue = new LinkedHashMap<>();
		MetaExpression option = mock(MetaExpression.class);
		when(option.getStringValue()).thenReturn("non existing protocol");
		optionsValue.put("sslprotocol", option);

		MetaExpression options = mock(MetaExpression.class);
		when(options.getValue()).thenReturn(optionsValue);
		when(options.getType()).thenReturn(OBJECT);

		// run
		optionsFactory.processOptions(options);
	}

	@Test(expectedExceptions = RobotRuntimeException.class, expectedExceptionsMessageRegExp = "Invalid \"browser\" option.")
	public void testInvalidBrowserOption() throws Exception {
		// mock
		OptionsFactory optionsFactory = new OptionsFactory();

		// The url
		String urlValue = "This is an url";
		MetaExpression url = mock(MetaExpression.class);
		when(url.getStringValue()).thenReturn(urlValue);

		// The options
		LinkedHashMap<String, MetaExpression> optionsValue = new LinkedHashMap<>();
		MetaExpression option = mock(MetaExpression.class);
		when(option.getStringValue()).thenReturn("invalid browser");
		optionsValue.put("browser", option);

		MetaExpression options = mock(MetaExpression.class);
		when(options.getValue()).thenReturn(optionsValue);
		when(options.getType()).thenReturn(OBJECT);

		// run
		optionsFactory.processOptions(options);
	}

	@Test(expectedExceptions = RobotRuntimeException.class, expectedExceptionsMessageRegExp = "Unknow option: nonExistingOption")
	public void testUnknownOption() throws Exception {
		// mock
		OptionsFactory optionsFactory = new OptionsFactory();

		// The url
		String urlValue = "This is an url";
		MetaExpression url = mock(MetaExpression.class);
		when(url.getStringValue()).thenReturn(urlValue);

		// The options
		LinkedHashMap<String, MetaExpression> optionsValue = new LinkedHashMap<>();
		MetaExpression option = mock(MetaExpression.class);
		when(option.getStringValue()).thenReturn("non existing value");
		optionsValue.put("nonExistingOption", option);

		MetaExpression options = mock(MetaExpression.class);
		when(options.getValue()).thenReturn(optionsValue);
		when(options.getType()).thenReturn(OBJECT);

		// run
		optionsFactory.processOptions(options);
	}

	@Test(expectedExceptions = RobotRuntimeException.class, expectedExceptionsMessageRegExp = "Invalid options variable!")
	public void testNoObjectGiven() throws Exception {
		// mock
		OptionsFactory optionsFactory = new OptionsFactory();

		// The url
		String urlValue = "This is an url";
		MetaExpression url = mock(MetaExpression.class);
		when(url.getStringValue()).thenReturn(urlValue);

		// The options
		LinkedHashMap<String, MetaExpression> optionsValue = new LinkedHashMap<>();
		MetaExpression option = mock(MetaExpression.class);
		when(option.getStringValue()).thenReturn("non existing value");
		optionsValue.put("nonExistingOption", option);

		MetaExpression options = mock(MetaExpression.class);
		when(options.getValue()).thenReturn(optionsValue);
		when(options.getType()).thenReturn(ATOMIC);

		// run
		optionsFactory.processOptions(options);
	}
}
