package nl.xillio.xill.plugins.xml.constructs;

import nl.xillio.xill.TestUtils;
import nl.xillio.xill.api.data.XmlNode;
import org.testng.annotations.Test;

import nl.xillio.xill.api.components.MetaExpression;
import nl.xillio.xill.plugins.xml.services.NodeService;
import nl.xillio.xill.api.construct.ConstructContext;
import nl.xillio.xill.api.components.RobotID;

import java.io.File;

import static org.mockito.Mockito.*;
import static org.testng.Assert.*;

/**
 * Tests for the {@link FromFileConstructTest}
 *
 * @author Zbynek Hochmann
 */
public class FromFileConstructTest extends TestUtils {

	/**
	 * Test the process method under normal circumstances
	 */
	@Test
	public void testProcess() {
		// Mock
		File file = mock(File.class);
		RobotID robotID = mock(RobotID.class);
		when(robotID.getPath()).thenReturn(file);

		MetaExpression filenameVar = mock(MetaExpression.class);
		when(filenameVar.getStringValue()).thenReturn(".");

		ConstructContext context = mock(ConstructContext.class);
		when(context.getRobotID()).thenReturn(robotID);

		XmlNode xmlNode = mock(XmlNode.class);
		String text = "test";
		when(xmlNode.toString()).thenReturn(text);

		NodeService nodeService = mock(NodeService.class);
		when(nodeService.fromFile(any())).thenReturn(xmlNode);

		// Run
		MetaExpression result = FromFileConstruct.process(context, filenameVar, nodeService);

		// Verify
		verify(nodeService).fromFile(any());

		// Assert
		assertSame(result.getMeta(XmlNode.class), xmlNode);
	}
}