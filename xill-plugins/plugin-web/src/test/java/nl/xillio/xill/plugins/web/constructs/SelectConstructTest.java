package nl.xillio.xill.plugins.web.constructs;

import nl.xillio.xill.api.components.MetaExpression;
import nl.xillio.xill.api.components.ExpressionBuilderHelper;
import nl.xillio.xill.api.errors.RobotRuntimeException;
import nl.xillio.xill.plugins.web.data.NodeVariable;
import nl.xillio.xill.plugins.web.services.web.WebService;
import org.testng.Assert;
import org.testng.annotations.Test;

import static org.mockito.Mockito.*;

/**
 * Tests the {@link SelectConstruct}
 */
public class SelectConstructTest extends ExpressionBuilderHelper {

    /**
     * Test the process under normal circumstances.
     */
    @Test
    public void testProcessNormalUsage() {
        // mock
        WebService webService = mock(WebService.class);

        // The element
        NodeVariable nodeVariable = mock(NodeVariable.class);
        MetaExpression node = mock(MetaExpression.class);
        when(node.getMeta(NodeVariable.class)).thenReturn(nodeVariable);

        // The select boolean
        MetaExpression select = mock(MetaExpression.class);
        when(select.getBooleanValue()).thenReturn(true);

        // the process
        when(webService.isSelected(nodeVariable)).thenReturn(false);

        // run
        MetaExpression output = SelectConstruct.process(node, select, webService);

        // verify
        verify(node, times(2)).getMeta(NodeVariable.class);
        verify(webService, times(1)).isSelected(nodeVariable);
        verify(webService, times(1)).click(nodeVariable);

        // assert
        Assert.assertEquals(output, NULL);
    }

    /**
     * Test the process with null page given.
     */
    @Test
    public void testNullInput() {
        // mock
        WebService webService = mock(WebService.class);
        MetaExpression page = mock(MetaExpression.class);
        MetaExpression element = mock(MetaExpression.class);
        when(page.isNull()).thenReturn(true);

        // run
        MetaExpression output = SelectConstruct.process(page, element, webService);

        // assert
        Assert.assertEquals(output, NULL);
    }

    /**
     * Tests the process when there is no need to take action.
     */
    @Test
    public void testProcessNoNeedToClick() {
        // mock
        WebService webService = mock(WebService.class);

        // The element
        NodeVariable nodeVariable = mock(NodeVariable.class);
        MetaExpression node = mock(MetaExpression.class);
        when(node.getMeta(NodeVariable.class)).thenReturn(nodeVariable);

        // The select boolean
        MetaExpression select = mock(MetaExpression.class);
        when(select.getBooleanValue()).thenReturn(true);

        // the process
        when(webService.isSelected(nodeVariable)).thenReturn(true);

        // run
        MetaExpression output = SelectConstruct.process(node, select, webService);

        // verify
        verify(node, times(2)).getMeta(NodeVariable.class);
        verify(webService, times(1)).isSelected(nodeVariable);
        verify(webService, times(0)).click(nodeVariable);

        // assert
        Assert.assertEquals(output, NULL);
    }

    /**
     * Tests the process when no node is given.
     */
    @Test(expectedExceptions = RobotRuntimeException.class, expectedExceptionsMessageRegExp = "Invalid variable type. NODE type expected!")
    public void testProcessNoNodeGiven() {
        // mock
        WebService webService = mock(WebService.class);

        // The element
        MetaExpression node = mock(MetaExpression.class);
        when(node.getMeta(NodeVariable.class)).thenReturn(null);

        // The select boolean
        MetaExpression select = mock(MetaExpression.class);

        // run
        SelectConstruct.process(node, select, webService);
    }

    /**
     * Tests the process when the WebService breaks.
     */
    @Test(expectedExceptions = RobotRuntimeException.class, expectedExceptionsMessageRegExp = "Failed to access NODE correctly")
    public void testProcessWebServiceFailure() {
        // mock
        WebService webService = mock(WebService.class);

        // The element
        NodeVariable nodeVariable = mock(NodeVariable.class);
        MetaExpression node = mock(MetaExpression.class);
        when(node.getMeta(NodeVariable.class)).thenReturn(nodeVariable);

        // The select boolean
        MetaExpression select = mock(MetaExpression.class);
        when(select.getBooleanValue()).thenReturn(false);

        // the process
        when(webService.isSelected(nodeVariable)).thenThrow(new RobotRuntimeException("I broke."));

        // run
        SelectConstruct.process(node, select, webService);
    }

}
