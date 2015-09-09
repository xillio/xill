package nl.xillio.xill.plugins.xml.constructs;

import org.testng.annotations.Test;

import nl.xillio.xill.api.components.MetaExpression;
import nl.xillio.xill.plugins.xml.data.XmlNode;
import nl.xillio.xill.api.errors.RobotRuntimeException;

import static org.mockito.Mockito.*;
import static org.testng.Assert.*;

/**
 * Tests for the {@link ToStringConstructTest}
 *
 * @author Zbynek Hochmann
 */
public class ToStringConstructTest {

	/**
	 * Test the process method under normal circumstances
	 */
	@Test
	public void testProcess() {
		String text = "xml";

		// Mock
		XmlNode xmlNode = mock(XmlNode.class);
		when(xmlNode.getXmlContent()).thenReturn(text);

		MetaExpression xmlNodeVar = mock(MetaExpression.class);
		when(xmlNodeVar.getMeta(XmlNode.class)).thenReturn(xmlNode);

		// Run
		MetaExpression result = ToStringConstruct.process(xmlNodeVar);

		// Verify
		verify(xmlNode).getXmlContent();

		// Assert
		assertSame(result.getStringValue(), text);
	}

	/**
	 * Test the process method when node input value is null
	 */
	@Test(expectedExceptions = RobotRuntimeException.class, expectedExceptionsMessageRegExp = "Expected node to be a XML node")
	public void testProcessNull() {

		// Mock
		MetaExpression xmlNodeVar = mock(MetaExpression.class);
		when(xmlNodeVar.isNull()).thenReturn(true);

		// Run
		GetTextConstruct.process(xmlNodeVar);
	}
}