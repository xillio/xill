package nl.xillio.xill.plugins.xml.constructs;

import nl.xillio.xill.api.data.XmlNode;
import org.testng.annotations.Test;

import nl.xillio.xill.api.components.MetaExpression;
import nl.xillio.xill.plugins.xml.services.XpathService;
import nl.xillio.xill.api.errors.RobotRuntimeException;
import nl.xillio.xill.plugins.xml.utils.MockUtils;

import java.util.ArrayList;

import static org.mockito.Mockito.*;
import static org.testng.Assert.*;

/**
 * Tests for the {@link XpathConstructTest}
 *
 * @author Zbynek Hochmann
 */
public class XpathConstructTest {

	/**
	 * Test the process method under normal circumstances
	 */
	@Test
	public void testProcess() {
		// Mock
		XmlNode returnXmlNode = mock(XmlNode.class);

		@SuppressWarnings("unchecked")
		ArrayList<Object> returnList = mock(ArrayList.class);
		when(returnList.size()).thenReturn(1);
		when(returnList.get(0)).thenReturn(returnXmlNode);

		XpathService xpathService = mock(XpathService.class);
		when(xpathService.xpath(any(), anyString(), any())).thenReturn(returnList);

		XmlNode xmlNode = mock(XmlNode.class);
		MetaExpression xmlNodeVar = mock(MetaExpression.class);
		when(xmlNodeVar.getMeta(XmlNode.class)).thenReturn(xmlNode);

		String text = "xpath selector";
		MetaExpression textVar = MockUtils.mockStringExpression(text);

		// Run
		MetaExpression result = XPathConstruct.process(xmlNodeVar, textVar, MockUtils.mockNullExpression(), xpathService);

		// Verify
		verify(xpathService).xpath(any(), anyString(), any());

		// Assert
		assertSame(result.getMeta(XmlNode.class), returnXmlNode);
	}

	/**
	 * Test the process when node input value is null
	 */
	@Test(expectedExceptions = RobotRuntimeException.class, expectedExceptionsMessageRegExp = "Expected node to be a XML node")
	public void testProcessNodeNull() {

		// Mock
		XpathService xpathService = mock(XpathService.class);

		String text = "xpath selector";
		MetaExpression textVar = MockUtils.mockStringExpression(text);

		MetaExpression xmlNodeVar = mock(MetaExpression.class);
		when(xmlNodeVar.isNull()).thenReturn(true);

		// Run
		XPathConstruct.process(xmlNodeVar, textVar, MockUtils.mockNullExpression(), xpathService);
	}
}