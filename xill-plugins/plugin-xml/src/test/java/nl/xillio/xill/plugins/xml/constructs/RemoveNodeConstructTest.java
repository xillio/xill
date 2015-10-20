package nl.xillio.xill.plugins.xml.constructs;

import nl.xillio.xill.api.data.XmlNode;
import org.testng.annotations.Test;

import nl.xillio.xill.api.components.MetaExpression;
import nl.xillio.xill.plugins.xml.services.NodeService;
import nl.xillio.xill.api.errors.RobotRuntimeException;

import static org.mockito.Mockito.*;
import static org.testng.Assert.*;

/**
 * Tests for the {@link RemoveNodeConstructTest}
 *
 * @author Zbynek Hochmann
 */
public class RemoveNodeConstructTest {

	/**
	 * Test the process method under normal circumstances
	 */
	@Test
	public void testProcess() {
		// Mock
		NodeService nodeService = mock(NodeService.class);

		XmlNode xmlNode = mock(XmlNode.class);
		MetaExpression xmlNodeVar = mock(MetaExpression.class);
		when(xmlNodeVar.getMeta(XmlNode.class)).thenReturn(xmlNode);

		// Run
		MetaExpression result = RemoveNodeConstruct.process(xmlNodeVar, nodeService);

		// Verify
		verify(nodeService).removeNode(any());
		
		// Assert
		assertTrue(result.isNull());
	}

	/**
	 * Test the process when node input value is null
	 */
	@Test(expectedExceptions = RobotRuntimeException.class, expectedExceptionsMessageRegExp = "Expected node to be a XML node")
	public void testProcessNodeNull() {

		// Mock
		NodeService nodeService = mock(NodeService.class);

		MetaExpression xmlNodeVar = mock(MetaExpression.class);
		when(xmlNodeVar.isNull()).thenReturn(true);

		// Run
		RemoveNodeConstruct.process(xmlNodeVar, nodeService);
	}
}