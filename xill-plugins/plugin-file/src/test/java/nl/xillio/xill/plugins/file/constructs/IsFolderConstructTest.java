package nl.xillio.xill.plugins.file.constructs;

import junit.framework.Assert;
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

import static org.mockito.Mockito.any;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * Tests the IsFileConstruct.
 *
 * @author Paul van der Zandt, Xillio
 */
public class IsFolderConstructTest extends TestUtils {

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
     *
     *
     * @throws IOException
     */
    @Test
    public void testProcess() throws IOException {
        setFileResolverReturnValue(new File(""));

        when(metaExpression.getStringValue()).thenReturn("");
        when(fileUtilities.isFile(any())).thenReturn(true);

        MetaExpression result = IsFileConstruct.process(constructContext, fileUtilities, metaExpression);

        Assert.assertTrue(result.getBooleanValue());
        verify(fileUtilities, times(1)).isFile(any());
    }

    @Test(expectedExceptions = RobotRuntimeException.class, expectedExceptionsMessageRegExp = "File not found, or not accessible")
    public void testProcessIOException() throws Exception {

        doThrow(new FileNotFoundException("")).when(fileUtilities).isFile(any(File.class));

        IsFileConstruct.process(constructContext, fileUtilities, metaExpression);

        verify(fileUtilities, times(1)).isFile(any(File.class));

    }
}