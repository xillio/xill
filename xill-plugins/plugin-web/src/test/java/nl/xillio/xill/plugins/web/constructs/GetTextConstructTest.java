package nl.xillio.xill.plugins.web.constructs;

import nl.xillio.xill.api.components.MetaExpression;
import nl.xillio.xill.api.components.ExpressionBuilderHelper;
import nl.xillio.xill.api.errors.RobotRuntimeException;
import nl.xillio.xill.plugins.web.data.NodeVariable;
import nl.xillio.xill.plugins.web.data.PageVariable;
import nl.xillio.xill.plugins.web.services.web.WebService;
import org.testng.Assert;
import org.testng.annotations.Test;

import java.util.Arrays;

import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyString;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.*;

/**
 * test the {@link GetTextConstruct}.
 */
public class GetTextConstructTest extends ExpressionBuilderHelper {

    /**
     * test the construct with normal input.
     * The input is a list with two values. One containing a NODE and with an input tag.
     * The other a PAGE with a textarea tag.
     */
    @Test
    public void testProcessNormalUsage() {
        // mock
        WebService webService = mock(WebService.class);

        // the first element in the list and what it uses
        MetaExpression first = mock(MetaExpression.class);
        NodeVariable nodeVariable = mock(NodeVariable.class);
        when(first.getMeta(NodeVariable.class)).thenReturn(nodeVariable);

        // the second element in the list and what it uses
        MetaExpression second = mock(MetaExpression.class);
        PageVariable pageVariable = mock(PageVariable.class);
        when(second.getMeta(NodeVariable.class)).thenReturn(null);
        when(second.getMeta(PageVariable.class)).thenReturn(pageVariable);

        // the process method
        MetaExpression elementList = mock(MetaExpression.class);
        when(elementList.isNull()).thenReturn(false);
        when(elementList.getType()).thenReturn(LIST);
        when(elementList.getValue()).thenReturn(Arrays.asList(first, second));

        // the processItem method for the first variable
        when(webService.getTagName(nodeVariable)).thenReturn("input");
        when(webService.getAttribute(eq(nodeVariable), anyString())).thenReturn("pet");

        // the processItem method for the second variable
        when(webService.getText(pageVariable)).thenReturn("master");

        // run
        MetaExpression output = GetTextConstruct.process(elementList, webService);

        // verify

        // the process method
        verify(elementList, times(1)).isNull();
        verify(elementList, times(1)).getType();
        verify(elementList, times(1)).getValue();

        // the processItemMethod for the first variable
        verify(first, times(2)).getMeta(NodeVariable.class);
        verify(webService, times(1)).getTagName(nodeVariable);
        verify(webService, times(1)).getAttribute(eq(nodeVariable), anyString());

        // the processItemMethod for the second variable
        verify(second, times(1)).getMeta(NodeVariable.class);
        verify(second, times(3)).getMeta(PageVariable.class);
        verify(webService, times(0)).getTagName(pageVariable);
        verify(webService, times(0)).getAttribute(eq(pageVariable), anyString());
        verify(webService, times(1)).getText(any());

        // assert
        Assert.assertEquals(output.getStringValue(), "petmaster");
    }

    /**
     * Test the process with null input given.
     */
    @Test(expectedExceptions = RobotRuntimeException.class)
    public void testNullInput() {
        // mock
        WebService webService = mock(WebService.class);
        MetaExpression input = mock(MetaExpression.class);
        when(input.isNull()).thenReturn(true);

        // run
        MetaExpression output = GetTextConstruct.process(input, webService);

        // assert
        Assert.assertEquals(output, NULL);
    }

    /**
     * test the construct with normal input.
     * The input is a ATOMIC object with no required tag.
     */
    @Test
    public void testProcessNoListWithNoTag() {
        // mock
        WebService webService = mock(WebService.class);

        // the input
        MetaExpression first = mock(MetaExpression.class);
        NodeVariable nodeVariable = mock(NodeVariable.class);
        when(first.getMeta(NodeVariable.class)).thenReturn(nodeVariable);

        // the process method
        when(first.isNull()).thenReturn(false);
        when(first.getType()).thenReturn(ATOMIC);

        // the processItem method for the variable
        when(webService.getTagName(nodeVariable)).thenReturn("No Input or TextArea");
        when(webService.getText(eq(nodeVariable))).thenReturn("pet");

        // run
        MetaExpression output = GetTextConstruct.process(first, webService);

        // verify

        // the process method
        verify(first, times(1)).isNull();
        verify(first, times(1)).getType();
        verify(first, times(0)).getValue();

        // the processItemMethod for the first variable
        verify(first, times(2)).getMeta(NodeVariable.class);
        verify(webService, times(2)).getTagName(nodeVariable);
        verify(webService, times(0)).getAttribute(any(), anyString());
        verify(webService, times(1)).getText(nodeVariable);

        // assert
        Assert.assertEquals(output.getStringValue(), "pet");
    }

    /**
     * test the construct when no node is given.
     */
    @Test(expectedExceptions = RobotRuntimeException.class, expectedExceptionsMessageRegExp = "Invalid variable type. NODE or PAGE expected.")
    public void testProcessNoNodeGiven() {
        // mock
        WebService webService = mock(WebService.class);
        // the input
        MetaExpression input = mock(MetaExpression.class);
        when(input.getMeta(NodeVariable.class)).thenReturn(null);

        // run
        GetTextConstruct.process(input, webService);
    }
}
