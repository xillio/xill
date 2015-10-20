package nl.xillio.xill.plugins.xml.constructs;

import nl.xillio.xill.api.data.XmlNode;
import org.testng.annotations.Test;

import nl.xillio.xill.api.components.MetaExpression;
import nl.xillio.xill.plugins.xml.services.NodeService;
import nl.xillio.xill.api.errors.RobotRuntimeException;
import nl.xillio.xill.plugins.xml.utils.MockUtils;

import static org.mockito.Mockito.*;
import static org.testng.Assert.*;

/**
 * Tests for the {@link MoveNodeConstructTest}
 *
 * @author Zbynek Hochmann
 */
public class MoveNodeConstructTest {

	/**
	 * Test the process method under normal circumstances
	 */
	@Test
	public void testProcess() {
		// Mock
		NodeService nodeService = mock(NodeService.class);

		XmlNode parentXmlNode = mock(XmlNode.class);
		MetaExpression parentXmlNodeVar = mock(MetaExpression.class);
		when(parentXmlNodeVar.getMeta(XmlNode.class)).thenReturn(parentXmlNode);

		XmlNode subXmlNode = mock(XmlNode.class);
		MetaExpression subXmlNodeVar = mock(MetaExpression.class);
		when(subXmlNodeVar.getMeta(XmlNode.class)).thenReturn(subXmlNode);

		// Run
		MetaExpression result = MoveNodeConstruct.process(parentXmlNodeVar, subXmlNodeVar, MockUtils.mockNullExpression(), nodeService);

		// Verify
		verify(nodeService).moveNode(any(), any(), any());

		// Assert
		assertTrue(result.isNull());
	}

	/**
	 * Test the process when the first input value is null
	 */
	@Test(expectedExceptions = RobotRuntimeException.class, expectedExceptionsMessageRegExp = "Expected node to be a XML node")
	public void testProcessNullParent() {
		// Mock
		NodeService nodeService = mock(NodeService.class);

		XmlNode subXmlNode = mock(XmlNode.class);
		MetaExpression subXmlNodeVar = mock(MetaExpression.class);
		when(subXmlNodeVar.getMeta(XmlNode.class)).thenReturn(subXmlNode);

		// Run
		MoveNodeConstruct.process(MockUtils.mockNullExpression(), subXmlNodeVar, MockUtils.mockNullExpression(), nodeService);
	}

	/**
	 * Test the process when the second input value is null
	 */
	@Test(expectedExceptions = RobotRuntimeException.class, expectedExceptionsMessageRegExp = "Expected node to be a XML node")
	public void testProcessNullSub() {
		// Mock
		NodeService nodeService = mock(NodeService.class);

		XmlNode parentXmlNode = mock(XmlNode.class);
		MetaExpression parentXmlNodeVar = mock(MetaExpression.class);
		when(parentXmlNodeVar.getMeta(XmlNode.class)).thenReturn(parentXmlNode);

		// Run
		MoveNodeConstruct.process(parentXmlNodeVar, MockUtils.mockNullExpression(), MockUtils.mockNullExpression(), nodeService);
	}
}