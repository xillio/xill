package nl.xillio.xill.plugins.stream.constructs;

import nl.xillio.xill.TestUtils;
import nl.xillio.xill.api.components.MetaExpression;
import nl.xillio.xill.api.construct.ConstructProcessor;
import nl.xillio.xill.api.errors.RobotRuntimeException;
import nl.xillio.xill.api.io.IOStream;
import nl.xillio.xill.api.io.SimpleIOStream;
import org.apache.commons.io.IOUtils;
import org.apache.commons.io.output.ByteArrayOutputStream;
import org.testng.annotations.Test;

import java.io.IOException;
import java.io.InputStream;

import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyInt;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.testng.Assert.assertEquals;


public class WriteConstructTest extends TestUtils {
    private final WriteConstruct construct = new WriteConstruct();

    @Test
    public void testReadWriteNoLimit() {
        // Create input data
        String inputText = "Hello world, this is a stream test";
        InputStream inputStream = IOUtils.toInputStream(inputText);
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        IOStream ioStream = new SimpleIOStream(inputStream, outputStream, "UnitTest");
        MetaExpression input = fromValue(ioStream);

        MetaExpression result = process(
                construct,
                input,
                input
        );

        assertEquals(outputStream.toString(), inputText);
        assertEquals(result.getNumberValue().intValue(), inputText.length());
    }

    @Test
    public void testReadWriteWithLimit() {
        int limit = 20;

        // Create input data
        String inputText = "This test will test the write construct with a limit";
        InputStream inputStream = IOUtils.toInputStream(inputText);
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        IOStream ioStream = new SimpleIOStream(inputStream, outputStream, "UnitTest");
        MetaExpression input = fromValue(ioStream);

        MetaExpression result = process(
                construct,
                input,
                input,
                fromValue(limit)
        );

        assertEquals(outputStream.toString(), inputText.substring(0, limit));
        assertEquals(result.getNumberValue().intValue(), limit);
    }

    @Test(expectedExceptions = RobotRuntimeException.class)
    public void testIOExceptionWhileWriting() throws IOException {
        // In this test we will pass an already closed input stream that will cause an io exception
        // Create input data
        InputStream inputStream = mock(InputStream.class);
        doThrow(IOException.class).when(inputStream).read(any(), anyInt(), anyInt());
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        IOStream ioStream = new SimpleIOStream(inputStream, outputStream, "UnitTest");
        MetaExpression input = fromValue(ioStream);

        process(
                construct,
                input,
                input
        );
    }

    @Test
    public void testWriteString() {
        // Create input data
        String inputText = "This test will test the write construct with a limit";
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        IOStream ioStream = new SimpleIOStream(outputStream, "UnitTest");
        MetaExpression input = fromValue(inputText);
        MetaExpression output = fromValue(ioStream);

        MetaExpression result = process(
                construct,
                input,
                output
        );

        assertEquals(outputStream.toString(), inputText);
        assertEquals(result.getNumberValue().intValue(), inputText.length());
    }

    @Test(expectedExceptions = RobotRuntimeException.class, expectedExceptionsMessageRegExp = ".*number.*")
    public void testLimitIsNaN() {

        process(
                construct,
                NULL,
                NULL,
                fromValue("Hello World")
        );
    }


}
