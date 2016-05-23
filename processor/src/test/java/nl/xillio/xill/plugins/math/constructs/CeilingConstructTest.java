package nl.xillio.xill.plugins.math.constructs;

import nl.xillio.xill.api.components.ExpressionBuilderHelper;
import nl.xillio.xill.api.components.MetaExpression;
import nl.xillio.xill.plugins.math.services.math.MathOperations;
import nl.xillio.xill.plugins.math.services.math.MathOperationsImpl;
import org.testng.annotations.Test;

import static org.testng.Assert.assertEquals;

public class CeilingConstructTest extends ExpressionBuilderHelper {
    private MathOperations math = new MathOperationsImpl();

    // The given and expected values.
    private Number[][] tests = new Number[][]{
            {1, 1},
            {1.2, 2},
            {-3.14, -3},
            {2.8, 3}
    };

    /**
     * Test the process.
     */
    @Test
    public void testProcess() {
        for (Number[] num : tests) {
            MetaExpression result = CeilingConstruct.process(fromValue(num[0]), math);
            assertEquals(result.getNumberValue().doubleValue(), num[1].doubleValue());
        }
    }
}
