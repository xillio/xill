package nl.xillio.xill.api.construct;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;

import nl.xillio.xill.api.Debugger;
import nl.xillio.xill.api.NullDebugger;
import nl.xillio.xill.api.behavior.BooleanBehavior;
import nl.xillio.xill.api.behavior.NumberBehavior;
import nl.xillio.xill.api.behavior.StringBehavior;
import nl.xillio.xill.api.components.AtomicExpression;
import nl.xillio.xill.api.components.Expression;
import nl.xillio.xill.api.components.ImmutableLiteral;
import nl.xillio.xill.api.components.ListExpression;
import nl.xillio.xill.api.components.MetaExpression;
import nl.xillio.xill.api.components.ObjectExpression;
import nl.xillio.xill.api.components.Processable;

/**
 * This class contains various useful utility functions to create Expressions
 */
public class ExpressionBuilderHelper {
    private static final Debugger expressionDebugger = new NullDebugger();
    /**
     * The true literal
     */
    public static final MetaExpression TRUE = new ImmutableLiteral(new BooleanBehavior(true));
    /**
     * The false literal
     */
    public static final MetaExpression FALSE = new ImmutableLiteral(new BooleanBehavior(false));

    /**
     * The null literal
     */
    public static final MetaExpression NULL = new ImmutableLiteral(NullLiteral.Instance);

    /**
     * Create a new {@link IntegerLiteral}
     *
     * @param value
     * @return expression
     */
    public static MetaExpression fromValue(final int value) {
	return new AtomicExpression(new NumberBehavior(value));
    }

    /**
     * Create a new {@link DoubleLiteral}
     *
     * @param value
     * @return expression
     */
    public static MetaExpression fromValue(final double value) {
	return new AtomicExpression(value);
    }

    /**
     * Create a new {@link BooleanLiteral}
     *
     * @param value
     * @return expression
     */
    public static MetaExpression fromValue(final boolean value) {
	return value ? TRUE : FALSE;
    }

    /**
     * Create a new {@link StringLiteral}
     *
     * @param value
     * @return expression
     */
    public static MetaExpression fromValue(final String value) {
	return new AtomicExpression(new StringBehavior(value));
    }

    /**
     * Create a new {@link ListExpression}
     *
     * @param value
     * @return the expression
     */
    public static MetaExpression fromValue(final List<MetaExpression> value) {
	return new ListExpression(value).process(expressionDebugger).get();
    }

    /**
     * Create a new {@link ObjectExpression}
     *
     * @param value
     * @return the expression
     */
    public static MetaExpression fromValue(final LinkedHashMap<String, MetaExpression> value) {
	LinkedHashMap<Processable, Processable> procValue = new LinkedHashMap<>(value.size());

	value.forEach((key, expression) -> {
	    procValue.put(fromValue(key), expression);
	});

	return new ObjectExpression(procValue).process(expressionDebugger).get();
    }

    /**
     * Create a new {@link ListExpression} with no values
     *
     * @return the expression
     */
    public static MetaExpression emptyList() {
	return fromValue(new ArrayList<>());
    }

    /**
     * Create a new {@link ObjectExpression} with no values
     *
     * @return the expression
     */
    public static MetaExpression emptyObject() {
	return fromValue(new LinkedHashMap<>());
    }

    /**
     * This {@link Expression} represents a null in literal form
     */
    private static final class NullLiteral implements Expression {

	/**
	 * The single instance of the null literal
	 */
	static final NullLiteral Instance = new NullLiteral();

	private NullLiteral() {
	}

	@Override
	public Number getNumberValue() {
	    return 0;
	}

	@Override
	public String getStringValue() {
	    return "";
	}

	@Override
	public boolean getBooleanValue() {
	    return false;
	}

	@Override
	public boolean isNull() {
	    return true;
	}

	@Override
	public void close() throws Exception {
	}

    }
}
