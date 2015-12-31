package nl.xillio.xill.plugins.xml.constructs;

import nl.xillio.xill.api.components.MetaExpression;
import nl.xillio.xill.api.data.XmlNode;
import nl.xillio.xill.api.errors.RobotRuntimeException;
import nl.xillio.xill.plugins.xml.services.NodeService;
import nl.xillio.xill.plugins.xml.utils.MockUtils;
import org.testng.annotations.Test;

import static org.mockito.Mockito.*;
import static org.testng.Assert.assertFalse;

/**
 * Tests for the {@link RemoveAttributeConstructTest}
 *
 * @author Zbynek Hochmann
 */
public class RemoveAttributeConstructTest {

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

        String text = "test";
        MetaExpression textVar = MockUtils.mockStringExpression(text);

        // Run
        MetaExpression result = RemoveAttributeConstruct.process(xmlNodeVar, textVar, nodeService);

        // Verify
        verify(nodeService).removeAttribute(any(), anyString());

        // Assert
        assertFalse(result.getBooleanValue());
    }

    /**
     * Test the process when node input value is null
     */
    @Test(expectedExceptions = RobotRuntimeException.class, expectedExceptionsMessageRegExp = "Expected node to be a XML node")
    public void testProcessNodeNull() {

        // Mock
        NodeService nodeService = mock(NodeService.class);

        String text = "test";
        MetaExpression textVar = MockUtils.mockStringExpression(text);

        MetaExpression xmlNodeVar = mock(MetaExpression.class);
        when(xmlNodeVar.isNull()).thenReturn(true);

        // Run
        RemoveAttributeConstruct.process(xmlNodeVar, textVar, nodeService);
    }
}