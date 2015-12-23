package nl.xillio.xill.plugins.codec.decode.constructs;

import nl.xillio.xill.TestUtils;
import nl.xillio.xill.api.components.MetaExpression;
import nl.xillio.xill.api.errors.RobotRuntimeException;
import nl.xillio.xill.plugins.codec.decode.services.DecoderService;
import nl.xillio.xill.plugins.codec.decode.services.DecoderServiceImpl;
import org.testng.Assert;
import org.testng.annotations.Test;

/**
 * Unit tests for decoding strings
 *
 * @author Pieter Soels
 */
public class stringFromBase64ConstructTest extends TestUtils {

    @Test
    public void testNormalUsage() {
        //Initialize
        MetaExpression input = fromValue("VW5pdFRlc3RpbmdJc0Z1bg==");
        MetaExpression wantedResult = fromValue("UnitTestingIsFun");
        DecoderService service = new DecoderServiceImpl();
        StringFromBase64Construct construct = new StringFromBase64Construct(service);

        //Run
        MetaExpression result = construct.process(input);

        //Assert
        Assert.assertEquals(wantedResult, result);
    }

    @Test(expectedExceptions = RobotRuntimeException.class, expectedExceptionsMessageRegExp = ".*null.*")
    public void testNullValue() {
        //Initialize
        MetaExpression input = NULL;
        DecoderService service = new DecoderServiceImpl();
        StringFromBase64Construct construct = new StringFromBase64Construct(service);

        //Run
        construct.process(input);
    }
}
