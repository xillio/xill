package nl.xillio.xill.plugins.file.constructs;

import nl.xillio.xill.TestUtils;
import nl.xillio.xill.api.components.MetaExpression;
import nl.xillio.xill.api.construct.ConstructContext;
import nl.xillio.xill.api.errors.RobotRuntimeException;
import nl.xillio.xill.plugins.file.services.files.FileUtilities;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;

import static org.mockito.Mockito.*;
import static org.testng.Assert.assertTrue;

/**
 * Tests the IsLinkConstruct.
 *
 * @author Paul van der Zandt, Xillio
 */
public class IsLinkConstructTest extends TestUtils {

    private ConstructContext constructContext;
    private FileUtilities fileUtilities;
    private MetaExpression metaExpression;

    /**
     * Initialization method for mocked objects used in all methods.
     */
    @BeforeMethod
    public void initialize() {
        constructContext = mock(ConstructContext.class);
        fileUtilities = mock(FileUtilities.class);
        metaExpression = mock(MetaExpression.class);
    }

    /**
     * @throws IOException
     */
    @Test
    public void testProcess() throws IOException {
        setFileResolverReturnValue(new File(""));

        when(metaExpression.getStringValue()).thenReturn("");
        when(fileUtilities.isLink(any(File.class))).thenReturn(true);

        MetaExpression result = IsLinkConstruct.process(constructContext, fileUtilities, metaExpression);

        assertTrue(result.getBooleanValue());
        verify(fileUtilities, times(1)).isLink(any(File.class));
    }

    @Test(expectedExceptions = RobotRuntimeException.class, expectedExceptionsMessageRegExp = "File not found, or not accessible")
    public void testProcessIOException() throws Exception {

        doThrow(new FileNotFoundException("")).when(fileUtilities).isLink(any(File.class));

        IsLinkConstruct.process(constructContext, fileUtilities, metaExpression);

        verify(fileUtilities, times(1)).isLink(any(File.class));

    }
}
