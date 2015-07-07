package nl.xillio.xill.components.expressions;

import nl.xillio.xill.components.instructions.FunctionDeclaration;

/**
 * This interface represents an Expression that takes a function as a parameter
 */
public interface FunctionParameterExpression {
    /**
     * Set the functional parameter
     * @param function
     */
    public void setFunction(FunctionDeclaration function);
}
