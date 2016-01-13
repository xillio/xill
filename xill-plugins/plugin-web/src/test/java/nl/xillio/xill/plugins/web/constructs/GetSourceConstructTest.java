package nl.xillio.xill.plugins.web.constructs;

import nl.xillio.xill.api.components.MetaExpression;
import nl.xillio.xill.api.components.ExpressionBuilderHelper;
import nl.xillio.xill.api.errors.RobotRuntimeException;
import nl.xillio.xill.plugins.web.data.NodeVariable;
import nl.xillio.xill.plugins.web.data.PageVariable;
import nl.xillio.xill.plugins.web.services.web.WebService;
import org.testng.Assert;
import org.testng.annotations.Test;

import static org.mockito.Matchers.any;
import static org.mockito.Mockito.*;

/**
 * test the {@link GetSourceConstruct}.
 */
public class GetSourceConstructTest extends ExpressionBuilderHelper {

    /**
     * test the construct with normal input.
      */
    @Test
    public void testProcessNormalUsage() {

        String html = "HTML source";

        // mock
        WebService webService = mock(WebService.class);

        MetaExpression page = fromValue("MyPage");
        PageVariable pageVariable = mock(PageVariable.class);
        page.storeMeta(pageVariable);

        when(webService.getSource(pageVariable)).thenReturn(html);

        // run
        MetaExpression output = GetSourceConstruct.process(page, webService);

        // verify

        // the processItemMethod for the second variable
        verify(webService, times(1)).getSource(any());

        // assert
        Assert.assertEquals(output.getStringValue(), html);
    }

    /**
     * Test the process with null input given.
     */
    @Test(expectedExceptions = RobotRuntimeException.class)
    public void testNullInput() {
        // mock
        WebService webService = mock(WebService.class);

        // run
        MetaExpression output = GetSourceConstruct.process(NULL, webService);

        // assert
        Assert.assertEquals(output, NULL);
    }

    /**
     * test the construct when no page is given.
     */
    @Test(expectedExceptions = RobotRuntimeException.class, expectedExceptionsMessageRegExp = "Invalid variable type. PAGE type expected!")
    public void testProcessNoPageGiven() {
        // mock
        WebService webService = mock(WebService.class);
        // the input
        MetaExpression input = fromValue("No Page");

        // run
        GetSourceConstruct.process(input, webService);
    }
}
