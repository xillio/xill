package nl.xillio.xill.plugins.file.constructs;

import me.biesaart.utils.FileUtilsService;
import nl.xillio.xill.TestUtils;
import nl.xillio.xill.api.components.MetaExpression;
import nl.xillio.xill.api.components.RobotID;
import nl.xillio.xill.api.construct.ConstructContext;
import nl.xillio.xill.api.errors.RobotRuntimeException;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;

import static org.mockito.Matchers.any;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.*;
import static org.testng.Assert.assertEquals;

/**
 * Test the {@link GetTextConstruct}
 */
public class GetTextConstructTest extends TestUtils {

    private static final String TEST_PATH = "This is a path", RESULT_TEXT = "Result";

    private MetaExpression filePath, encoding;
    private File file;
    private ConstructContext context;
    private FileUtilsService fileUtils;

    /**
     * Mock needed variables
     *
     * @throws IOException
     */
    @BeforeMethod
    public void initialize() throws IOException {
        // File
        filePath = mock(MetaExpression.class);
        mockExpression(ATOMIC, false, 0, TEST_PATH);
        file = mock(File.class);
        setFileResolverReturnValue(file);

        // Context
        RobotID robotID = mock(RobotID.class);
        context = mock(ConstructContext.class);
        when(context.getRobotID()).thenReturn(robotID);

        // FileUtils
        fileUtils = mock(FileUtilsService.class);


        // Charset
        String charset = "UTF-8";
        encoding = mockExpression(ATOMIC, false, 0, charset);
    }

    /**
     * Test {@link GetTextConstruct#process(ConstructContext, FileUtilsService, MetaExpression, MetaExpression)} under normal circumstances
     *
     * @throws IOException
     * @throws Exception
     */
    @Test
    public void testProcessNormal() throws IOException {
        // Mock
        when(fileUtils.readFileToString(any(File.class), any(Charset.class))).thenReturn(RESULT_TEXT);

        // Run the Method
        MetaExpression result = GetTextConstruct.process(context, fileUtils, filePath, encoding);

        // Verify
        verify(fileUtils).readFileToString(eq(file), any(Charset.class));

        // Assert
        assertEquals(result.getStringValue(), RESULT_TEXT);
    }

    /**
     * Test {@link GetTextConstruct#process(ConstructContext, FileUtilsService, MetaExpression, MetaExpression)} when no encoding is given.
     *
     * @throws IOException
     */
    @Test
    public void testProcessNoCharset() throws IOException {
        // Mock
        when(fileUtils.readFileToString(any(File.class))).thenReturn(RESULT_TEXT);

        MetaExpression nullExpression = mockExpression(ATOMIC);
        when(nullExpression.isNull()).thenReturn(true);

        // Run
        MetaExpression result = GetTextConstruct.process(context, fileUtils, filePath, nullExpression);

        // Verify
        verify(fileUtils).readFileToString(eq(file));

        // Assert
        assertEquals(result.getStringValue(), RESULT_TEXT);
    }

    /**
     * Test {@link GetTextConstruct#process(ConstructContext, FileUtilsService, MetaExpression, MetaExpression)} when an invalid encoding is given
     */
    @Test(expectedExceptions = RobotRuntimeException.class)
    public void testProcessInvalidCharset() {
        // Mock
        MetaExpression invalidCharset = mockExpression(ATOMIC, false, 0, "INVALID");

        // Run the Method
        GetTextConstruct.process(context, fileUtils, filePath, invalidCharset);
    }
}
