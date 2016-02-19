package nl.xillio.xill.plugins.codec.decode.services;

import nl.xillio.xill.TestUtils;
import org.testng.annotations.Test;

import static org.testng.Assert.assertEquals;

/**
 * This is the test class for the
 *
 * @author Pieter Dirk Soels
 */
public class DecoderServiceImplTest extends TestUtils {

    private final DecoderService decoderService = new DecoderServiceImpl();

    @Test
    public void testUnescapeXML() {
        String text = "&lt;test&gt;&quot;&amp;";
        int passes = 5;

        String result = decoderService.unescapeXML(text, passes);

        String expectedOutput = "<test>\"&";
        assertEquals(expectedOutput, result);
    }
}