package nl.xillio.xill.plugins.math.services.math;

import com.google.inject.Singleton;

/**
 * This is the main implementation of {@link MathOperations}
 */
@Singleton
public class MathOperationImpl implements MathOperations {

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

}
