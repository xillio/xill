package test.integration;


import nl.xillio.xill.components.instructions.ExpressionInstruction;
import org.testng.annotations.Test;

public class Robot {

    @Test
    public void run() {
        ExpressionInstruction instruction = new ExpressionInstruction(null);
        instruction.getChildren();
    }
}
