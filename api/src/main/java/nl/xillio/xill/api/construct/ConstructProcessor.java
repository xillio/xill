package nl.xillio.xill.api.construct;

import java.util.Arrays;
import java.util.Optional;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.function.Supplier;

import org.apache.commons.lang3.StringUtils;

import nl.xillio.xill.api.QuadFunction;
import nl.xillio.xill.api.TriFunction;
import nl.xillio.xill.api.components.MetaExpression;

/**
 * This class represents a one-time running process created by a construct with a process method and input.
 *
 *
 */
public class ConstructProcessor {
	private final Function<MetaExpression[], MetaExpression> processor;
	private Predicate<Argument[]> requirementsValidator;
	private final Argument[] parameters;

	/**
	 * Create a process from input and operation
	 *
	 * @param processor
	 *        The function used to process the input
	 * @param parameters
	 *        The expected parameters with default values
	 */
	public ConstructProcessor(final Function<MetaExpression[], MetaExpression> processor, final Argument[] parameters) {
		this.processor = processor;
		this.parameters = parameters;

		// By default there will be no validation.
		// The processor will always require all parameters to be evaluated.
		requirementsValidator = a -> false;
	}

	/**
	 * Create a single-argument processor from input and operation
	 *
	 * @param processor
	 *        The function used to process the input
	 * @param parameter
	 *        The expected parameter
	 */
	public ConstructProcessor(final Function<MetaExpression, MetaExpression> processor, final Argument parameter) {
		this(args -> processor.apply(args[0]), new Argument[] {parameter});
	}

	/**
	 * Create a two-argument processor from input and operation
	 *
	 * @param processor
	 *        The function used to process the input
	 * @param firstArgument
	 *        The first parameter
	 * @param secondArgument
	 *        The second parameter
	 */
	public ConstructProcessor(final BiFunction<MetaExpression, MetaExpression, MetaExpression> processor, final Argument firstArgument, final Argument secondArgument) {
		this(args -> processor.apply(args[0], args[1]), new Argument[] {firstArgument, secondArgument});
	}

	/**
	 * Create a three-argument processor from input and operation
	 *
	 * @param processor
	 *        The function used to process the input
	 * @param firstArgument
	 *        The first parameter
	 * @param secondArgument
	 *        The second parameter
	 * @param thirdArgument
	 *        The third parameter
	 */
	public ConstructProcessor(final TriFunction<MetaExpression, MetaExpression, MetaExpression, MetaExpression> processor, final Argument firstArgument, final Argument secondArgument,
					final Argument thirdArgument) {
		this(args -> processor.apply(args[0], args[1], args[2]), new Argument[] {firstArgument, secondArgument, thirdArgument});
	}

	/**
	 * Create a four-argument processor from input and operation
	 *
	 * @param processor
	 *        The function used to process the input
	 * @param firstArgument
	 *        The first parameter
	 * @param secondArgument
	 *        The second parameter
	 * @param thirdArgument
	 *        The third parameter
	 * @param forthArgument
	 *        The forth parameter
	 */
	public ConstructProcessor(final QuadFunction<MetaExpression, MetaExpression, MetaExpression, MetaExpression, MetaExpression> processor, final Argument firstArgument, final Argument secondArgument,
					final Argument thirdArgument, final Argument forthArgument) {
		this(args -> processor.apply(args[0], args[1], args[2], args[3]), new Argument[] {firstArgument, secondArgument, thirdArgument, forthArgument});
	}

	/**
	 * Creates a process without input.
	 *
	 * @param processor
	 *        The function used to process the input
	 */
	public ConstructProcessor(final Supplier<MetaExpression> processor) {
		this(a -> processor.get(), new Argument[] {});
	}

	/**
	 * Sets the value of a named argument.
	 *
	 * @param name
	 *        the name of the argument
	 * @param value
	 *        the value to set the argument to
	 * @return True if the argument was set
	 */
	public boolean setArgument(final String name, final MetaExpression value) {
		for (Argument arg : parameters) {
			if (arg.getName().equals(name)) {
				arg.setValue(value);
				return true;
			}
		}
		return false;
	}

	/**
	 * Sets the value of the argument in slot index.
	 *
	 * @param index
	 *        the index of the argument from 0 to {@link ConstructProcessor#getNumberOfArguments()} - 1
	 * @param value
	 *        the value to set the argument to
	 * @return True if the argument was set, false if it was rejected
	 */
	public boolean setArgument(final int index, final MetaExpression value) {
		if (parameters.length <= index || index < 0) {
			return false;
		}

		parameters[index].setValue(value);
		return true;
	}

	/**
	 * Checks if the processor can start processing. Without adding all arguments.
	 *
	 * @return true if processing can be started.
	 */
	public boolean assertEvaluationSuccess() {
		return requirementsValidator.test(parameters);
	}

	/**
	 * Finds the first missing argument.
	 *
	 * @return The first missing argument or null when there are no missing arguments
	 */
	public Optional<String> getMissingArgument() {
		return Arrays.stream(parameters).parallel().filter(argument -> !argument.isSet()).map(Argument::getName).findAny();
	}

	/**
	 * Runs the process and return the result.
	 *
	 * @return the result
	 */
	public MetaExpression process() {
		MetaExpression[] vars = new MetaExpression[parameters.length];
		int i = 0;
		for (Argument arg : parameters) {
			vars[i++] = arg.getValue();
		}

		return processor.apply(vars);
	}

	/**
	 * @return the number of accepted arguments
	 */
	public int getNumberOfArguments() {
		return parameters.length;
	}

	@Override
	public String toString() {
		return toString("process" + hashCode());
	}

	/**
	 * Gets a string representation with a friendly name.
	 *
	 * @param name
	 *        the friendly construct name
	 * @return a string representation of the construct
	 */
	public String toString(final String name) {
		return name + "(" + StringUtils.join(parameters, ", ") + ");";
	}

	/**
	 * Gets the name of an argument.
	 *
	 * @param i
	 *        the index of the argument to get the name of
	 * @return the name of the argument
	 */
	public String getArgumentName(final int i) {
		return parameters[i].getName();
	}

	/**
	 * Set the requirements validator for the processor to use.
	 * By default this is a method that checks if all arguments are set.
	 * This validator can be used to enforce lazy evaluation.
	 *
	 * @param validator
	 *        the validator that should be used
	 */
	public void setRequirementValidation(final Predicate<Argument[]> validator) {
		requirementsValidator = validator;
	}

	/**
	 * Clears all arguments
	 */
	public void reset() {
		for (Argument arg : parameters) {
			arg.clear();
		}
	}

	/**
	 * Shortcut for processing a processor. It pushes the provided arguments and calls {@link ConstructProcessor#process()}
	 *
	 * @param processor
	 *        the processor to process
	 * @param arguments
	 *        the arguments to pass to the processor
	 * @return the result
	 */
	public static MetaExpression process(final ConstructProcessor processor, final MetaExpression... arguments) {

		int i = 0;
		for (MetaExpression arg : arguments) {
			processor.setArgument(i++, arg);
		}

		return processor.process();
	}
}
