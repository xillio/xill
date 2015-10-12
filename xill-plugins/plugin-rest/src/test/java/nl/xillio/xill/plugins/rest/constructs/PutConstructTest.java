package nl.xillio.xill.plugins.rest.constructs;

import java.util.HashMap;

import nl.xillio.xill.api.data.XmlNode;
import org.testng.annotations.Test;

import nl.xillio.xill.api.components.ExpressionDataType;
import nl.xillio.xill.api.components.MetaExpression;
import nl.xillio.xill.api.errors.RobotRuntimeException;
import nl.xillio.xill.plugins.rest.services.RestService;
import nl.xillio.xill.plugins.rest.data.Content;
import static org.mockito.Mockito.*;
import static org.testng.Assert.*;

/**
 * Tests for the {@link PutConstructTest}
 *
 * @author Zbynek Hochmann
 */
public class PutConstructTest {

	/**
	 * Test the process method under normal circumstances
	 */
	@Test
	public void testProcess() {
		// Mock
		String url = "www.resturl.com/uri";
		MetaExpression urlVar = mock(MetaExpression.class);
		when(urlVar.getStringValue()).thenReturn(url);

		MetaExpression optionsVar = mock(MetaExpression.class);
		when(optionsVar.isNull()).thenReturn(true);

		XmlNode returnXmlNode = mock(XmlNode.class);
		MetaExpression returnContent = mock(MetaExpression.class);
		when(returnContent.getMeta(XmlNode.class)).thenReturn(returnXmlNode);
		Content content = mock(Content.class);
		when(content.getMeta()).thenReturn(returnContent);

		MetaExpression bodyVar = mock(MetaExpression.class);
		when(bodyVar.isNull()).thenReturn(false);
		when(bodyVar.getType()).thenReturn(ExpressionDataType.OBJECT);
		when(bodyVar.toString()).thenReturn("body content");

		RestService restService = mock(RestService.class);
		when(restService.put(anyString(), any(), any())).thenReturn(content);

		// Run
		MetaExpression result = PutConstruct.process(urlVar, optionsVar, bodyVar, restService);

		// Verify
		verify(restService).put(anyString(), any(), any());

		// Assert
		assertSame(result.getMeta(XmlNode.class), returnXmlNode);
	}

	/**
	 * Test the process when URL input value is null
	 */
	@Test(expectedExceptions = RobotRuntimeException.class, expectedExceptionsMessageRegExp = "URL is empty!")
	public void testProcessUrlNull() {
		// Mock
		RestService restService = mock(RestService.class);

		MetaExpression urlVar = mock(MetaExpression.class);
		when(urlVar.getStringValue()).thenReturn("");

		MetaExpression optionsVar = mock(MetaExpression.class);
		when(optionsVar.isNull()).thenReturn(true);

		MetaExpression bodyVar = mock(MetaExpression.class);

		// Run
		PutConstruct.process(urlVar, optionsVar, bodyVar, restService);
	}

	/**
	 * Test the process when the options contains invalid option
	 */
	@Test(expectedExceptions = RobotRuntimeException.class, expectedExceptionsMessageRegExp = "Option .* is invalid!")
	public void testProcessInvalidOption() {
		// Mock
		RestService restService = mock(RestService.class);

		String url = "www.resturl.com/uri";
		MetaExpression urlVar = mock(MetaExpression.class);
		when(urlVar.getStringValue()).thenReturn(url);

		HashMap<String, MetaExpression> optionList = new HashMap<>();
		optionList.put("unsupported-option", null);

		MetaExpression optionsVar = mock(MetaExpression.class);
		when(optionsVar.isNull()).thenReturn(false);
		when(optionsVar.getType()).thenReturn(ExpressionDataType.OBJECT);
		when(optionsVar.getValue()).thenReturn(optionList);

		MetaExpression bodyVar = mock(MetaExpression.class);

		// Run
		PutConstruct.process(urlVar, optionsVar, bodyVar, restService);
	}
}