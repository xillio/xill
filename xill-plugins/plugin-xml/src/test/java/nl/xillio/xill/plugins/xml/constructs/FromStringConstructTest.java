package nl.xillio.xill.plugins.xml.constructs;

import nl.xillio.xill.api.data.XmlNode;
import org.testng.annotations.Test;

import nl.xillio.xill.api.components.MetaExpression;
import nl.xillio.xill.plugins.xml.services.NodeService;
import nl.xillio.xill.plugins.xml.utils.MockUtils;

import static org.mockito.Mockito.*;
import static org.testng.Assert.*;

/**
 * Tests for the {@link FromStringConstructTest}
 *
 * @author Zbynek Hochmann
 */
public class FromStringConstructTest {

	/**
	 * Test the process method under normal circumstances
	 */
	@Test
	public void testProcess() {
		// Mock
		String text = "xml test";
		XmlNode xmlNode = mock(XmlNode.class);
		when(xmlNode.toString()).thenReturn(text);

		NodeService nodeService = mock(NodeService.class);
		when(nodeService.fromString(anyString())).thenReturn(xmlNode);

		// Run
		MetaExpression result = FromStringConstruct.process(MockUtils.mockStringExpression(text), nodeService);

		// Verify
		verify(nodeService).fromString(any());

		// Assert
		assertSame(result.getMeta(XmlNode.class), xmlNode);
	}

}
