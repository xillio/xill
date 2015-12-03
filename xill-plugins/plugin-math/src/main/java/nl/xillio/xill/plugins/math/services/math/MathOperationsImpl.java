package nl.xillio.xill.plugins.math.services.math;

import com.google.inject.Singleton;
import nl.xillio.xill.api.behavior.NumberBehavior;
import nl.xillio.xill.api.components.MetaExpression;
import nl.xillio.xill.api.errors.NotImplementedException;

/**
 * This is the main implementation of {@link MathOperations}
 */
@Singleton
public class MathOperationsImpl implements MathOperations {

	@Override
	public double abs(final Number value) {

		if (value instanceof Integer) {
			return Math.abs(value.intValue());
		} else if (value instanceof Long) {
			return Math.abs(value.longValue());
		} else if (value instanceof Float) {
			return Math.abs(value.floatValue());
		} else {
			return Math.abs(value.doubleValue());
		}
	}

	@Override
	public long round(final Number value) {
		if (value instanceof Integer) {
			return value.intValue();
		} else if (value instanceof Long) {
			return value.longValue();
		} else if (value instanceof Float) {
			return Math.round(value.floatValue());
		} else {
			return Math.round(value.doubleValue());
		}
	}

	@Override
	public double random() {
		return Math.random();

	}

	@Override
	public long random(final long value) {
		return (long) (Math.random() * value);
	}

	@Override
	public boolean isNumber(final MetaExpression value){
		switch (value.getType()) {
			case ATOMIC:
				Object behaviour = value.getValue();
				return behaviour != null && (behaviour instanceof NumberBehavior);
			case LIST:
			case OBJECT:
				return false;
			default: throw new NotImplementedException("This type has not been implemented.");
		}
	}
}
