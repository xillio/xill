package nl.xillio.xill.plugins.math.services;

import nl.xillio.xill.api.components.MetaExpression;
import nl.xillio.xill.api.construct.ExpressionBuilderHelper;
import nl.xillio.xill.plugins.math.services.math.MathOperationsImpl;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;

import org.testng.Assert;
import org.testng.annotations.Test;

/**
 * Unit test class for the service MathOperationsImpl.
 *
 * @author Pieter Soels.
 */
public class MathOperationsImplTest {

    @Test
    public void testIsNumber(){
        //Initialize
        MathOperationsImpl math = new MathOperationsImpl();
        List<MetaExpression> list = new ArrayList<>();
        LinkedHashMap<String, MetaExpression> object = new LinkedHashMap<>();

        //Mock
        MetaExpression intValue = ExpressionBuilderHelper.fromValue(5);
        list.add(intValue);
        object.put("test", intValue);
        MetaExpression doubleValue = ExpressionBuilderHelper.fromValue(5.05);
        MetaExpression stringNumberValue = ExpressionBuilderHelper.fromValue("1");
        MetaExpression stringTextValue = ExpressionBuilderHelper.fromValue("Test");
        MetaExpression boolValue = ExpressionBuilderHelper.fromValue(true);
        MetaExpression nullValue = ExpressionBuilderHelper.fromValue((String)null);
        MetaExpression listValue = ExpressionBuilderHelper.fromValue(list);
        MetaExpression objectValue = ExpressionBuilderHelper.fromValue(object);

        //Assert
        Assert.assertTrue(math.isNumber(intValue));
        Assert.assertTrue(math.isNumber(doubleValue));
        Assert.assertTrue(math.isNumber(stringNumberValue));
        Assert.assertTrue(!math.isNumber(stringTextValue));
        Assert.assertTrue(math.isNumber(boolValue));
        Assert.assertTrue(!math.isNumber(nullValue));
        Assert.assertTrue(!math.isNumber(listValue));
        Assert.assertTrue(!math.isNumber(objectValue));
    }
}
