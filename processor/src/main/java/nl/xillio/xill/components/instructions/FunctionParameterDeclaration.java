package nl.xillio.xill.components.instructions;

import nl.xillio.xill.CodePosition;
import nl.xillio.xill.api.Debugger;
import nl.xillio.xill.api.components.*;

/**
 * This is a {@link VariableDeclaration}, but with adjusted behaviours specific to function parameters. 
 *
 * @author Geert Konijnendijk
 */
public class FunctionParameterDeclaration extends VariableDeclaration{
    public FunctionParameterDeclaration(Processable expression, String name, Robot robot) {
        super(expression, name, robot);
    }

    public FunctionParameterDeclaration(Processable expression, String name) {
        super(expression, name);
    }

    @Override
    protected int getInsertionIndex(Debugger debugger) {
        // Function parameters are assigned on the stack frame above the function, but should be inserted in the function
        return debugger.getStackDepth() + 1;
    }

    /**
     * A variable declared to be null.
     *
     * @param position The position in code where the null variable occurs.
     * @param name     The name of the variable that is declared to be null.
     * @return A declaration with value {@link ExpressionBuilder#NULL}
     */
    public static FunctionParameterDeclaration nullDeclaration(final CodePosition position, final String name) {
        FunctionParameterDeclaration dec = new FunctionParameterDeclaration(ExpressionBuilderHelper.NULL, name);
        dec.setPosition(position);

        return dec;
    }
}
