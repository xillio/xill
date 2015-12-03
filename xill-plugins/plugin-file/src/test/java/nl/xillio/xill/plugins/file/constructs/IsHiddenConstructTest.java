package nl.xillio.xill.plugins.file.constructs;

import nl.xillio.xill.TestUtils;
import nl.xillio.xill.api.components.MetaExpression;
import nl.xillio.xill.api.components.RobotID;
import nl.xillio.xill.api.construct.ConstructContext;
import nl.xillio.xill.plugins.file.services.files.FileUtilities;
import org.apache.logging.log4j.Logger;
import org.testng.annotations.Test;

import java.io.File;
import java.io.IOException;

import static org.mockito.Mockito.*;

/**
 * Tests the IsHidden construct.
 *
 * Created by Anwar on 12/1/2015.
 */
public class IsHiddenConstructTest extends TestUtils {

    @Test
    public void testProcess() throws IOException {

        ConstructContext constructContext = mock(ConstructContext.class);

        FileUtilities fileUtilities = mock(FileUtilities.class);

        MetaExpression metaExpression = mock(MetaExpression.class);
        when(metaExpression.getStringValue()).thenReturn("");

        setFileResolverReturnValue(new File(""));

        IsHiddenConstruct.process(constructContext, fileUtilities, metaExpression);

        verify(fileUtilities, times(1)).isHidden(any());
    }

    @Test
    public void testProcessIOException() throws Exception {

        MetaExpression metaExpression = mock(MetaExpression.class);

        Logger logger = mock(Logger.class);
        RobotID robotID = mock(RobotID.class);
        ConstructContext context = mock(ConstructContext.class);
        when(context.getRobotID()).thenReturn(robotID);
        when(context.getRootLogger()).thenReturn(logger);

        setFileResolverReturnValue(new File(""));

        FileUtilities fileUtils = mock(FileUtilities.class);
        doThrow(new IOException("")).when(fileUtils).isHidden(any(File.class));

        // Run the method
        IsHiddenConstruct.process(context, fileUtils, metaExpression);

        // Verify the error that was logged
        verify(logger).error(contains(""), any(IOException.class));

    }
}