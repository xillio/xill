package nl.xillio.xill.components.expressions;

import nl.xillio.xill.api.components.Processable;
import nl.xillio.xill.components.instructions.FunctionDeclaration;

/**
 * This interface represents an Expression that takes a function as a parameter
 */
public interface FunctionParameterExpression extends Processable {
    /**
     * Set the functional parameter
     *
     * @param function  the value to which the function declaration needs to be set.
     */
    public void setFunction(FunctionDeclaration function);
}
