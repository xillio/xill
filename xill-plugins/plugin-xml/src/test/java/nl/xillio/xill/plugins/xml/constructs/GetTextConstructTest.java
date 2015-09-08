package nl.xillio.xill.plugins.xml.constructs;

import nl.xillio.xill.api.components.MetaExpression;
import nl.xillio.xill.plugins.xml.data.XmlNode;
import nl.xillio.xill.api.errors.RobotRuntimeException;

import org.w3c.dom.Node;
import org.testng.annotations.Test;

import static org.mockito.Mockito.*;
import static org.testng.Assert.*;

/**
 * Tests for the {@link GetTextConstructTest}
 *
 * @author Zbynek Hochmann
 */
public class GetTextConstructTest {

	/**
	 * Test the process method under normal circumstances
	 */
	@Test
	public void testProcess() {
		String text = "hello";

		// Mock
		Node node = mock(Node.class);
		when(node.getTextContent()).thenReturn(text);

		XmlNode xmlNode = mock(XmlNode.class);
		when(xmlNode.getNode()).thenReturn(node);

		MetaExpression xmlNodeVar = mock(MetaExpression.class);
		when(xmlNodeVar.getMeta(XmlNode.class)).thenReturn(xmlNode);

		// Run
		MetaExpression result = GetTextConstruct.process(xmlNodeVar);

		// Verify
		verify(xmlNode).getNode();
		verify(node).getTextContent();

		// Assert
		assertSame(result.getStringValue(), text);
	}

	/**
	 * Test the process when node input value is null
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