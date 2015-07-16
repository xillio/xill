package nl.xillio.xill.api.components;

import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.function.Supplier;

import nl.xillio.xill.api.Debugger;
import nl.xillio.xill.api.construct.ExpressionBuilderHelper;
import nl.xillio.xill.api.errors.RobotRuntimeException;

/**
 * This is a utility class that will build expressions from values
 */
public class ExpressionBuilder extends ExpressionBuilderHelper implements Processable {

    private final Supplier<MetaExpression> expressionSupplier;

    /**
     * Create a new {@link ExpressionBuilder} that will produce a double
     *
     * @param value the value to set
     */
    public ExpressionBuilder(final double value) {
	expressionSupplier = () -> fromValue(value);
    }

    /**
     * Create a new {@link ExpressionBuilder} that will produce an integer
     *
     * @param value the value to set
     */
    public ExpressionBuilder(final int value) {
	expressionSupplier = () -> fromValue(value);
    }

    /**
     * Create a new {@link ExpressionBuilder} that will produce a string
     *
     * @param value the value to set
     */
    public ExpressionBuilder(final String value) {
	expressionSupplier = () -> fromValue(value);
    }

    /**
     * Create a new {@link ExpressionBuilder} that will produce a List
     *
     * @param value the value to set
     */
    public ExpressionBuilder(final List<MetaExpression> value) {
	expressionSupplier = () -> fromValue(value);
    }

    /**
     * Create a new {@link ExpressionBuilder} that will produce an object
     *
     * @param value the value to set
     */
    public ExpressionBuilder(final LinkedHashMap<String, MetaExpression> value) {
	expressionSupplier = () -> fromValue(value);
    }

    /**
     * Create a new {@link ExpressionBuilder} that will produce a double
     *
     * @param value the value to set
     */
    public ExpressionBuilder(final boolean value) {
	expressionSupplier = () -> fromValue(value);
    }

    @Override
    public InstructionFlow<MetaExpression> process(final Debugger debugger) throws RobotRuntimeException {
	return InstructionFlow.doResume(expressionSupplier.get());
    }

    @Override
    public Collection<Processable> getChildren() {
	return new ArrayList<>();
    }
}
