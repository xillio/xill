package nl.xillio.xill.plugins.file.constructs;

import me.biesaart.utils.IOUtilsService;
import nl.xillio.xill.TestUtils;
import nl.xillio.xill.api.components.MetaExpression;
import nl.xillio.xill.api.construct.ConstructProcessor;
import nl.xillio.xill.api.errors.RobotRuntimeException;
import nl.xillio.xill.api.io.IOStream;
import nl.xillio.xill.api.io.SimpleIOStream;
import nl.xillio.xill.plugins.file.services.files.FileStreamFactory;
import org.apache.commons.io.IOUtils;
import org.apache.commons.io.output.ByteArrayOutputStream;
import org.testng.annotations.Test;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.AccessDeniedException;
import java.nio.file.Files;
import java.nio.file.Path;

import static org.mockito.Matchers.any;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.spy;
import static org.testng.Assert.assertEquals;


public class WriteConstructTest extends TestUtils {

    @Test
    public void testNormalUsageStreams() {
        // Input
        String text = "This is my content";
        InputStream input = IOUtils.toInputStream(text);
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        IOStream ioStream = new SimpleIOStream(input, outputStream, "test");
        MetaExpression wrapper = fromValue(ioStream);

        // Process
        WriteConstruct construct = new WriteConstruct(new IOUtilsService(), null);

        MetaExpression result = ConstructProcessor.process(
                construct.prepareProcess(context(construct)),
                wrapper,
                wrapper
        );

        // Assertions
        assertEquals(outputStream.toString(), text);
        assertEquals(result.getNumberValue().intValue(), text.length());
    }

    @Test
    public void testNormalUsageFiles() throws IOException {
        String text = "Here is the content";
        Path targetFile = Files.createTempFile(getClass().getSimpleName(), ".test");
        MetaExpression input = fromValue(text);
        MetaExpression target = fromValue(targetFile.toAbsolutePath().toString());

        WriteConstruct construct = new WriteConstruct(new IOUtilsService(), new FileStreamFactory());

        setFileResolverReturnValue(targetFile);
        MetaExpression result = ConstructProcessor.process(
                construct.prepareProcess(context(construct)),
                input,
                target
        );

        assertEquals(Files.readAllLines(targetFile).get(0), text);
        assertEquals(result.getNumberValue().intValue(), text.length());
        Files.delete(targetFile);
    }

    @Test(expectedExceptions = RobotRuntimeException.class, expectedExceptionsMessageRegExp = ".*denied.*My File.*")
    public void testAccessDenied() throws IOException {
        WriteConstruct construct = spy(new WriteConstruct(new IOUtilsService(), null));
        doThrow(new AccessDeniedException("My File")).when(construct).write(any(), any(), any());

        ConstructProcessor.process(
                construct.prepareProcess(context(construct)),
                fromValue("MOCK INPUT"),
                fromValue("MOCK INPUT")
        );
    }

    @Test(expectedExceptions = RobotRuntimeException.class, expectedExceptionsMessageRegExp = ".*MOCK INPUT.*")
    public void testOtherIOException() throws IOException {
        WriteConstruct construct = spy(new WriteConstruct(new IOUtilsService(), null));
        doThrow(IOException.class).when(construct).write(any(), any(), any());

        ConstructProcessor.process(
                construct.prepareProcess(context(construct)),
                fromValue("MOCK INPUT"),
                fromValue("MOCK INPUT")
        );
    }
}