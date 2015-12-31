package nl.xillio.xill.plugins.math.constructs;

import nl.xillio.xill.TestUtils;
import nl.xillio.xill.api.components.MetaExpression;
import nl.xillio.xill.api.errors.RobotRuntimeException;
import org.testng.Assert;
import org.testng.annotations.Test;

import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/**
 * Test the {@link HungarianAlgorithmConstruct}.
 */
public class HungarianAlgorithmConstructTest extends TestUtils {

    /**
     * <p>
     * Test the process method under normal circumstances.
     * </p>
     * <p>
     * We not only test if the output has the correct type but also the correct value.
     * </p>
     * <b>Nothing to verify</b>
     */
    @Test
    public void processNormalUsage() {
        // Mock
        MetaExpression matrix = mock(MetaExpression.class);
        String hungarianReturnValue = "[{\"sum\":10.0},{\"cells\":[{\"row\":0,\"col\":2},{\"row\":1,\"col\":1},{\"row\":2,\"col\":0}]}]";
        when(matrix.getType()).thenReturn(LIST);
        when(matrix.getValue()).thenReturn(Arrays.asList(
                fromValue(Arrays.asList(fromValue(0), fromValue(1), fromValue(3))),
                fromValue(Arrays.asList(fromValue(2), fromValue(2), fromValue(3))),
                fromValue(Arrays.asList(fromValue(5), fromValue(4), fromValue(1)))));

        MetaExpression max = mock(MetaExpression.class);
        when(max.getBooleanValue()).thenReturn(true);

        // Run
        MetaExpression result = HungarianAlgorithmConstruct.process(matrix, max);

        // Verify
        // nothing to verify

        // Assert
        // Check wheter the result is correct as String value.
        Assert.assertEquals(result.toString(), hungarianReturnValue);

        // Check if the result is a list, if so, check if its children are objects
        Assert.assertEquals(result.getType(), LIST);
        @SuppressWarnings("unchecked")
        List<MetaExpression> resultAsList = (List<MetaExpression>) result.getValue();
        Assert.assertEquals(resultAsList.size(), 2);
        Assert.assertEquals(resultAsList.get(0).getType(), OBJECT);
        Assert.assertEquals(resultAsList.get(1).getType(), OBJECT);

        // Check wheter the second object contains a list, if so, check if its children are objects
        @SuppressWarnings("unchecked")
        LinkedHashMap<String, MetaExpression> secondItem = (LinkedHashMap<String, MetaExpression>) resultAsList.get(1).getValue();
        Assert.assertEquals(secondItem.get("cells").getType(), LIST);
        @SuppressWarnings("unchecked")
        List<MetaExpression> secondItemAsList = (List<MetaExpression>) secondItem.get("cells").getValue();
        Assert.assertEquals(secondItemAsList.size(), 3);
        Assert.assertEquals(secondItemAsList.get(0).getType(), OBJECT);
        Assert.assertEquals(secondItemAsList.get(1).getType(), OBJECT);
        Assert.assertEquals(secondItemAsList.get(2).getType(), OBJECT);
    }

    /**
     * Tests wheter the exception is thrown when there are too little rows provided.
     */
    @Test(expectedExceptions = RobotRuntimeException.class, expectedExceptionsMessageRegExp = "Not enough data, Expected at least 1 row with data")
    public void processTooFewRows() {

        MetaExpression matrix = mock(MetaExpression.class);
        when(matrix.getType()).thenReturn(LIST);
        when(matrix.getValue()).thenReturn(Arrays.asList());

        MetaExpression max = mock(MetaExpression.class);
        when(max.getBooleanValue()).thenReturn(true);

        HungarianAlgorithmConstruct.process(matrix, max);

    }

    /**
     * Tests wheter the exception is throwns when enough rows but too little columns are provided.
     */
    @Test(expectedExceptions = RobotRuntimeException.class, expectedExceptionsMessageRegExp = "Not enough data, Expected at least 1 column with data")
    public void processTooFewColumns() {
        MetaExpression matrix = mock(MetaExpression.class);
        when(matrix.getType()).thenReturn(LIST);
        when(matrix.getValue()).thenReturn(Arrays.asList(fromValue(Arrays.asList())));

        MetaExpression max = mock(MetaExpression.class);
        when(max.getBooleanValue()).thenReturn(true);

        HungarianAlgorithmConstruct.process(matrix, max);

    }

    /**
     * Tests wheter an exception is thrown when invalid input is provided.
     */
    @Test(expectedExceptions = RobotRuntimeException.class, expectedExceptionsMessageRegExp = "Invalid value `error` in matrix at \\[1,1\\]")
    public void processInvalidInputInMatrix() {
        // Mock
        MetaExpression matrix = mock(MetaExpression.class);
        when(matrix.getType()).thenReturn(LIST);
        when(matrix.getValue()).thenReturn(Arrays.asList(
                fromValue(Arrays.asList(fromValue(0), fromValue(1), fromValue(3))),
                fromValue(Arrays.asList(fromValue(2), fromValue("error"), fromValue(3))),
                fromValue(Arrays.asList(fromValue(5), fromValue(4), fromValue(1)))));

        MetaExpression max = mock(MetaExpression.class);
        when(max.getBooleanValue()).thenReturn(true);
        // Run
        HungarianAlgorithmConstruct.process(matrix, max);
    }
}
