package nl.xillio.xill.plugins.codec.encode.constructs;

import nl.xillio.xill.TestUtils;
import nl.xillio.xill.api.components.MetaExpression;
import nl.xillio.xill.api.errors.RobotRuntimeException;
import nl.xillio.xill.plugins.codec.encode.services.EncoderService;
import nl.xillio.xill.plugins.codec.encode.services.EncoderServiceImpl;
import org.testng.Assert;
import org.testng.annotations.Test;

/**
 * @author Pieter Soels
 */
public class stringToBase64ConstructTest extends TestUtils {

    @Test
    public void testNormalUsage() {
        //Initialize
        MetaExpression wantedResult = fromValue("VW5pdFRlc3RpbmdJc0Z1bg==");
        MetaExpression input = fromValue("UnitTestingIsFun");
        EncoderService service = new EncoderServiceImpl();
        StringToBase64Construct construct = new StringToBase64Construct(service);

        //Run
        MetaExpression result = construct.process(input);

        //Assert
        Assert.assertEquals(wantedResult, result);
    }

    @Test(expectedExceptions = RobotRuntimeException.class, expectedExceptionsMessageRegExp = ".*null.*")
    public void testNullValue() {
        //Initialize
        MetaExpression input = NULL;
        EncoderService service = new EncoderServiceImpl();
        StringToBase64Construct construct = new StringToBase64Construct(service);

        //Run
        construct.process(input);
    }
}
