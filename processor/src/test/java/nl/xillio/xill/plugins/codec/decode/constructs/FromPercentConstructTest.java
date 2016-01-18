package nl.xillio.xill.plugins.codec.decode.constructs;

import me.biesaart.utils.FileUtilsService;
import me.biesaart.utils.IOUtilsService;
import nl.xillio.xill.TestUtils;
import nl.xillio.xill.api.components.MetaExpression;
import nl.xillio.xill.api.construct.ConstructContext;
import nl.xillio.xill.api.construct.ConstructProcessor;
import nl.xillio.xill.plugins.codec.decode.services.DecoderService;
import nl.xillio.xill.plugins.codec.decode.services.DecoderServiceImpl;
import org.testng.annotations.Test;

import java.io.IOException;

import static org.mockito.Mockito.mock;
import static org.testng.Assert.assertEquals;

/**
 * This is a test class for testing the FromPercentConstruct
 *
 * @author Pieter Dirk Soels
 */
public class FromPercentConstructTest extends TestUtils {

    @Test
    public void testDecodeFromPercent() throws IOException {
        // Initialize
        MetaExpression EXPECTED_TEXT = fromValue("Th!s is my e*pected string \\/\\/!th s0/\\/\\e weird ch@r@cters :D");
        MetaExpression DEPLOY_PERCENT = fromValue("Th%21s+is+my+e%2Apected+string+%5C%2F%5C%2F%21th+s0%2F%5C%2F%5Ce+weird+ch%40r%40cters+%3AD");

        DecoderService decoderService = new DecoderServiceImpl(new FileUtilsService(), new IOUtilsService());
        FromPercentConstruct construct = new FromPercentConstruct(decoderService);
        ConstructContext context = mock(ConstructContext.class);

        ConstructProcessor processor = construct.prepareProcess(context);
        processor.setArgument(0, DEPLOY_PERCENT);

        // Run
        MetaExpression result = processor.process();

        // Assert
        assertEquals(result, EXPECTED_TEXT);
    }
}