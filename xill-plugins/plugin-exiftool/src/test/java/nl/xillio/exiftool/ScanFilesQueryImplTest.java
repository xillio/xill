package nl.xillio.exiftool;

import nl.xillio.exiftool.query.Projection;
import org.testng.annotations.Test;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.NoSuchFileException;
import java.nio.file.Path;
import java.nio.file.Paths;


public class ScanFilesQueryImplTest {

    @Test(expectedExceptions = IllegalArgumentException.class)
    public void testConstructorConstraintFile() throws IOException {
        Path file = Files.createTempDirectory("unittest");
        new ScanFileQueryImpl(file, new Projection(), new FileQueryOptionsImpl());
        Files.delete(file);
    }

    @Test(expectedExceptions = NoSuchFileException.class)
    public void testConstructorConstraintNotExist() throws NoSuchFileException {
        new ScanFileQueryImpl(Paths.get("I surely do not exist"), null, null);
    }

}
