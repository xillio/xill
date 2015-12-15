package nl.xillio.xill.components.expressions;

import nl.xillio.xill.api.Debugger;
import nl.xillio.xill.api.components.MetaExpression;
import nl.xillio.xill.api.components.MetaExpressionIterator;
import nl.xillio.xill.api.construct.ExpressionBuilderHelper;
import nl.xillio.xill.api.errors.RobotRuntimeException;
import nl.xillio.xill.components.instructions.FunctionDeclaration;

import java.util.*;

/**
 * @author Pieter Soels
 *
 * This class is for abstracting methods from the map- and filterexpression classes.
 */
public class MapFilterHandler {
    protected FunctionDeclaration functionDeclaration;

    public MapFilterHandler() { }

    /**
     * Instruction set for the process of atomics.
     * Run the function and return the result.
     * When function has one argument give the meta expression.
     * When the function has two arguments give an key (index) as well.
     */

    /**
     * Instruction set for the process of lists.
     * Run the function on each element and return the result.
     * When function has one argument give the meta expression.
     * When the function has two arguments give an key (index) as well.
     */

    /**
     * Instruction set for the process of objects.
     * Run the functions on each element and return the result.
     * When function has one argument give the value of the object
     * When the function has two arguments give the key as well.
     */
}
