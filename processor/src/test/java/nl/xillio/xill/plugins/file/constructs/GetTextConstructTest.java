package nl.xillio.xill.plugins.file.constructs;

import me.biesaart.utils.IOUtils;
import me.biesaart.utils.IOUtilsService;
import nl.xillio.xill.TestUtils;
import nl.xillio.xill.api.components.MetaExpression;
import nl.xillio.xill.api.construct.ConstructProcessor;
import nl.xillio.xill.api.errors.RobotRuntimeException;
import nl.xillio.xill.api.io.IOStream;
import nl.xillio.xill.api.io.SimpleIOStream;
import nl.xillio.xill.plugins.file.services.files.FileStreamFactory;
import org.testng.annotations.Test;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;

import static org.testng.Assert.assertEquals;


public class GetTextConstructTest extends TestUtils {
    @Test
    public void testNormalUsageFromStream() {
        IOStream stream = new SimpleIOStream(IOUtils.toInputStream("Hello World"), null);
        MetaExpression input = fromValue(stream);
        GetTextConstruct construct = new GetTextConstruct(null, new IOUtilsService());

        MetaExpression result = ConstructProcessor.process(
                construct.prepareProcess(context(construct)),
                input,
                fromValue("UTF-8")
        );

        assertEquals(result.getStringValue(), "Hello World");
    }

    @Test
    public void testNormalUsageFromFile() throws IOException {
        // Create test file
        Path file = Files.createTempFile(getClass().getSimpleName(), ".txt");
        Files.copy(IOUtils.toInputStream("File Test"), file, StandardCopyOption.REPLACE_EXISTING);

        GetTextConstruct construct = new GetTextConstruct(new FileStreamFactory(), new IOUtilsService());
        setFileResolverReturnValue(file);

        MetaExpression result = ConstructProcessor.process(
                construct.prepareProcess(context(construct)),
                fromValue(file.toAbsolutePath().toString())
        );

        assertEquals(result.getStringValue(), "File Test");

        // Delete test file
        Files.delete(file);
    }

    @Test(expectedExceptions = RobotRuntimeException.class, expectedExceptionsMessageRegExp = ".*I don't exist.*")
    public void testFromFileNotExists() {
        // Create test file
        Path file = Paths.get("I don't exist");

        GetTextConstruct construct = new GetTextConstruct(new FileStreamFactory(), new IOUtilsService());
        setFileResolverReturnValue(file);

        ConstructProcessor.process(
                construct.prepareProcess(context(construct)),
                fromValue(file.toAbsolutePath().toString())
        );
    }
}