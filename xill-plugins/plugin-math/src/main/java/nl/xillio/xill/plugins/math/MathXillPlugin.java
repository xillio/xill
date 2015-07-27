package nl.xillio.xill.plugins.math;

import com.google.inject.Binder;

import nl.xillio.plugins.XillPlugin;
import nl.xillio.xill.plugins.math.services.math.MathOperationImpl;
import nl.xillio.xill.plugins.math.services.math.MathOperations;

/**
 * This package includes all example constructs
 */
public class MathXillPlugin extends XillPlugin {

	@Override
	public void configure(final Binder binder) {
		//binder.bind(MathOperations.class).to(MathOperationImpl.class);
	}

}
