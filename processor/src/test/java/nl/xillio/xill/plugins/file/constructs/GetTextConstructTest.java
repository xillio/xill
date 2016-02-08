package nl.xillio.xill.plugins.file.constructs;

import me.biesaart.utils.IOUtils;
import nl.xillio.xill.api.io.IOStream;
import nl.xillio.xill.api.io.SimpleIOStream;
import org.testng.annotations.Test;

import static org.testng.Assert.*;


public class GetTextConstructTest {
    @Test
    public void testNormalUsageFromStream() {
        IOStream stream = new SimpleIOStream(IOUtils.toInputStream("Hello World"), null);
    }
}