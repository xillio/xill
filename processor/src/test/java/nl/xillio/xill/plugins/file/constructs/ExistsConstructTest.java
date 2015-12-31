package nl.xillio.xill.plugins.file.constructs;

import nl.xillio.xill.api.components.MetaExpression;
import nl.xillio.xill.api.components.RobotID;
import nl.xillio.xill.api.construct.ConstructContext;
import nl.xillio.xill.api.construct.ExpressionBuilderHelper;
import nl.xillio.xill.plugins.file.services.files.FileUtilities;
import org.testng.annotations.Test;

import static org.mockito.Mockito.*;
import static org.testng.Assert.assertSame;

/**
 * Test the ExistsConstruct
 */
public class ExistsConstructTest {

    @Test
    public void testProcessNormalTrueAndFalse() throws Exception {
        // URI
        String path = "This is the path";
        MetaExpression uri = mock(MetaExpression.class);
        when(uri.getStringValue()).thenReturn(path);

        // Context
        RobotID robotID = mock(RobotID.class);
        ConstructContext context = mock(ConstructContext.class);
        when(context.getRobotID()).thenReturn(robotID);

        // FileUtils
        FileUtilities fileUtils = mock(FileUtilities.class);
        when(fileUtils.exists(any())).thenReturn(true, false);

        // Run the method once for true
        MetaExpression result = ExistsConstruct.process(context, fileUtils, uri);

        // Verify
        verify(fileUtils).exists(any());

        // Assert
        assertSame(result, ExpressionBuilderHelper.TRUE);

        // Run the method again for false
        result = ExistsConstruct.process(context, fileUtils, uri);

        // Verify
        verify(fileUtils, times(2)).exists(any());

        // Assert
        assertSame(result, ExpressionBuilderHelper.FALSE);
    }
}
