package nl.xillio.xill.api.construct;

/**
 * This interface contains the core functionality for all constructs
 */
public interface Construct {
	/**
	 * Returns the name of the construct. This name is also the command by which this construct can be called inside scripts.
	 *
	 * @return the name of the construct. This name is also the command by which this construct can be called inside scripts
	 */
	public String getName();

	/**
	 * Build a new process and return it ready for input
	 *
	 * @param context
	 *        The context for which to prepare the {@link Construct}
	 * @return A prepared processor loaded with the {@link Construct} behaviour.
	 */
	public ConstructProcessor prepareProcess(final ConstructContext context);
}
