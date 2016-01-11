package test.integration;


import nl.xillio.xill.api.components.MetaExpression;
import org.testng.annotations.Test;

import static nl.xillio.xill.api.construct.ExpressionBuilderHelper.fromValue;

public class Robot {

    @Test
    public void run() {
        MetaExpression result = fromValue("hello");
    }
}
