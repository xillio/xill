package test.integration;


import nl.xillio.xill.api.behavior.StringBehavior;
import nl.xillio.xill.api.components.AtomicExpression;
import nl.xillio.xill.api.components.MetaExpression;
import nl.xillio.xill.components.instructions.ExpressionInstruction;
import org.testng.annotations.Test;

public class Robot {

    @Test
    public void run() {
        ExpressionInstruction instruction = new ExpressionInstruction(null);
        instruction.getChildren();

        MetaExpression expression = new AtomicExpression(new StringBehavior("Hello"));
    }
}
