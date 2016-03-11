package nl.xillio.xill.components.instructions;

import nl.xillio.xill.CodePosition;
import nl.xillio.xill.api.Debugger;
import nl.xillio.xill.api.components.*;
import nl.xillio.xill.api.errors.RobotRuntimeException;
import nl.xillio.xill.components.operators.Assign;

import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Stack;

/**
 * This {@link Instruction} represents the start of a variable's lifespan.
 * <p>
 * A VariableDeclaration can run in one of two modes: var or argument.
 * In var mode the value of this variable will be
 * set to the value after the assignment operator (or null).
 * In argument mode the value of this variable will be set to the argument passed to this robot, or use the expression
 * value as a fallback.
 *
 * @author Thomas biesaart
 */
public class VariableDeclaration extends Instruction {
    private final Processable assignation;
    private final Stack<MetaExpression> valueStack = new Stack<>();
    /**
     * This is here for debugging purposes.
     */
    private final String name;

    /**
     * Create a new {@link VariableDeclaration}.
     *
     * @param expression the default value expression
     * @param name       the name of this declaration for debugging purposes
     * @param robot      if this declaration is an 'argument' then the robot that holds the argument
     */
    public VariableDeclaration(final Processable expression, final String name, Robot robot) {
        this.name = name;
        Processable selectedExpression = robot == null ? expression : new ArgumentProvider(expression, robot);
        assignation = new Assign(this, Collections.emptyList(), selectedExpression);
    }

    public VariableDeclaration(Processable expression, String name) {
        this(expression, name, null);
    }

    @Override
    public InstructionFlow<MetaExpression> process(final Debugger debugger) {
        pushVariable(ExpressionBuilderHelper.NULL);
        assignation.process(debugger);

        return InstructionFlow.doResume();
    }

    /**
     * @return the expression of the variable or null
     */
    public MetaExpression getVariable() {
        if (!valueStack.isEmpty()) {
            return valueStack.peek();
        }
        return ExpressionBuilder.NULL;
    }

    /**
     * Set the value of the variable.
     *
     * @param value    The value to which the variable needs to be set.
     */
    public void replaceVariable(final MetaExpression value) {
        if (hasValue()){
            MetaExpression current = valueStack.pop();
            pushVariable(value);
            current.releaseReference();
        }else{
            throw new RobotRuntimeException("Reference to unknown variable '" + getName() +"', could not assign value.");
        }
    }

    /**
     * Set the value of the variable without popping the last one
     *
     * @param value    The value of the variable.
     */
    public void pushVariable(final MetaExpression value) {
        value.registerReference();
        valueStack.push(value);
    }

    /**
     * Release the current variable.
     */
    public void releaseVariable() {
        valueStack.pop().releaseReference();
    }

    /**
     * A variable declared to be null.
     *
     * @param position    The position in code where the null variable occurs.
     * @param name        The name of the variable that is declared to be null.
     * @return A declaration with value {@link ExpressionBuilder#NULL}
     */
    public static VariableDeclaration nullDeclaration(final CodePosition position, final String name) {
        VariableDeclaration dec = new VariableDeclaration(ExpressionBuilderHelper.NULL, name);
        dec.setPosition(position);

        return dec;
    }

    @Override
    public Collection<Processable> getChildren() {
        return Arrays.asList(assignation);
    }

    @Override
    public void close() throws Exception {
        releaseVariable();
    }

    /**
     * This name is for debugging purposes and is <b>NOT UNIQUE</b>.
     * Do not use as identifier
     *
     * @return the name
     */
    public String getName() {
        return name;
    }

    public boolean hasValue() {
        return !valueStack.empty();
    }

    public MetaExpression peek(int index) {
        return valueStack.elementAt(index);
    }

    /**
     * This class represents an expression that has a variable source. It is used to make argument declarations possible.
     *
     * @author Thomas biesaart
     */
    private class ArgumentProvider implements Processable {

        private final Processable expression;
        private final Robot robot;

        public ArgumentProvider(Processable expression, Robot robot) {
            this.expression = expression;
            this.robot = robot;
        }

        @Override
        public InstructionFlow<MetaExpression> process(Debugger debugger) {
            if (robot.hasArgument()) {
                return InstructionFlow.doResume(robot.getArgument());
            }

            return InstructionFlow.doResume(expression.process(debugger).get());
        }

        @Override
        public Collection<Processable> getChildren() {
            return Collections.singletonList(expression);
        }
    }
}
