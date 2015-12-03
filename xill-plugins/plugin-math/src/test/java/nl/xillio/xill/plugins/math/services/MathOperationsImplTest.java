package nl.xillio.xill.plugins.math.services;

import nl.xillio.xill.TestUtils;
import nl.xillio.xill.api.components.MetaExpression;
import nl.xillio.xill.api.construct.ExpressionBuilderHelper;
import nl.xillio.xill.plugins.math.services.math.MathOperationsImpl;

import java.time.ZonedDateTime;
import nl.xillio.xill.plugins.date.data.Date;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;

import org.testng.Assert;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

/**
 * Unit test class for the service MathOperationsImpl.
 *
 * @author Pieter Soels.
 */
public class MathOperationsImplTest extends TestUtils {

    @DataProvider(name = "isNumber")
    Object[][] isNumberTypes() {
        return new Object[][]{
                {fromValue(5.05), true},
                {fromValue("1"), true},
                {fromValue("Test"), false},
                {fromValue(Double.NEGATIVE_INFINITY), true},
                {fromValue(Double.POSITIVE_INFINITY), true},
                {fromValue(true), true},
                {fromValue((String) null), false},
                {emptyList(), false},
                {emptyObject(), false},
                {fromValue(initializeTestList()), false},
                {fromValue(initializeTestObject()), false},
                {fromValue(new Date(ZonedDateTime.now()).toString()), false}
        };
    }

    private LinkedHashMap<String, MetaExpression> initializeTestObject(){
        LinkedHashMap<String, MetaExpression> value = new LinkedHashMap<>();
        value.put("test", fromValue(1));
        return value;
    }

    private List<MetaExpression> initializeTestList(){
        List<MetaExpression> value = new ArrayList<>();
        value.add(fromValue(1));
        return value;
    }

    @Test(dataProvider = "isNumber")
    public void testIsNumber(MetaExpression value, boolean result){
        // Initialize
        MathOperationsImpl math = new MathOperationsImpl();

        // Assert
        Assert.assertEquals(math.isNumber(value), result);
    }
}
