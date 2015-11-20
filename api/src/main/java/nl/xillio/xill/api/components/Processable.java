package nl.xillio.xill.api.components;

import java.util.Collection;

import nl.xillio.xill.api.Debugger;
import nl.xillio.xill.api.errors.RobotRuntimeException;

/**
 * This interface represents an object that can be processed
 */
public interface Processable {

	/**
	 * Process this object
	 * 
	 * @param debugger
	 *        The debugger that should be used when processing this
	 *
	 * @return The return value is there is one
	 * @throws RobotRuntimeException
	 *         When processing went wrong
	 */
	@SuppressWarnings("squid:RedundantThrowsDeclarationCheck")
	// This RobotRuntimeException is not addressed, because it triggers specific behaviour in the editor.
	public InstructionFlow<MetaExpression> process(Debugger debugger) throws RobotRuntimeException;

	/**
	 * Collect all {@link Processable} used by this one. This is used to search through program trees
	 *
	 * @return all Children
	 */
	public Collection<Processable> getChildren();
}
