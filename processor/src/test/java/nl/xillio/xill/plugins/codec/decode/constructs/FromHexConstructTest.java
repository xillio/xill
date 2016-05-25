package nl.xillio.xill.plugins.codec.decode.constructs;

import nl.xillio.xill.TestUtils;
import nl.xillio.xill.api.components.MetaExpression;
import nl.xillio.xill.api.construct.ConstructContext;
import nl.xillio.xill.api.construct.ConstructProcessor;
import nl.xillio.xill.api.errors.InvalidUserInputException;
import nl.xillio.xill.api.errors.OperationFailedException;
import nl.xillio.xill.api.errors.RobotRuntimeException;
import nl.xillio.xill.plugins.codec.decode.services.DecoderServiceImpl;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import static org.mockito.Mockito.mock;
import static org.testng.Assert.*;

/**
 * Full test of FromHexConstruct.
 *
 * @author Paul van der Zandt
 * @since 3.0
 */
public class FromHexConstructTest extends TestUtils {

    private ConstructProcessor processor;

    @BeforeMethod
    public void initTest() {
        ConstructContext context = mock(ConstructContext.class);
        FromHexConstruct construct = new FromHexConstruct(new DecoderServiceImpl());
        processor = construct.prepareProcess(context);
    }

    @Test
    public void testPrepareProcess() throws Exception {
        processor.setArgument(0, fromValue("C3A4C3ABC384"));
        MetaExpression expression = processor.process();

        assertEquals(expression.getStringValue(), "äëÄ");
    }

    @Test
    public void testPrepareProcessCharacterset() throws Exception {
        processor.setArgument(0, fromValue("e4ebc4"));
        processor.setArgument(1, fromValue("ISO-8859-1"));
        MetaExpression expression = processor.process();
        assertEquals(expression.getStringValue(), "äëÄ");
    }

    @Test
    public void testPrepareProcessNull() throws Exception {
        processor.setArgument(0, NULL);
        MetaExpression expression = processor.process();
        assertTrue(expression.isNull());
    }

    @Test(expectedExceptions = OperationFailedException.class, expectedExceptionsMessageRegExp = "Could not convert hex to normal..*Illegal hexadecimal character w at index 1.*")
    public void testPrepareProcessIllegalValue() throws Exception {
        processor.setArgument(0, fromValue("ew"));
        processor.process();
        fail();
    }

    @Test(expectedExceptions = OperationFailedException.class, expectedExceptionsMessageRegExp = "Could not convert hex to normal..*Odd number of characters..*")
    public void testPrepareProcessIllegalValueOdd() throws Exception {
        processor.setArgument(0, fromValue("a"));
        processor.process();
        fail();
    }

    @Test(expectedExceptions = InvalidUserInputException.class, expectedExceptionsMessageRegExp = "Unknown character set..*DOES-NOT-EXIST.*")
    public void testPrepareProcessIllegalCharacterset() throws Exception {
        processor.setArgument(0, fromValue("30"));
        processor.setArgument(1, fromValue("DOES-NOT-EXIST"));
        processor.process();
        fail();
    }

}
