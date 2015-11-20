package nl.xillio.xill.api.components;

import java.util.List;

import nl.xillio.xill.api.errors.RobotRuntimeException;

/**
 * This interface represents a Robot
 */
public interface Robot extends InstructionSet {
	/**
	 * Use a BFS algorithm to find a target among the children
	 * 
	 * @param target
	 *        the target to calculate the path to from the root
	 * @return the path to the target or an empty list if the target wasn't found.
	 */
	public List<Processable> pathToInstruction(Instruction target);

	/**
	 * Initialize the robot to be used as a library
	 * 
	 * @throws RobotRuntimeException
	 *         when the library couldn't be initialized
	 */
	@SuppressWarnings("squid:RedundantThrowsDeclarationCheck")
	// The RobotRuntimeException will not be addressed as it triggers specific behaviour in the editor.
	public void initializeAsLibrary() throws RobotRuntimeException;

	/**
	 * Set the argument for this robot. This is used by the callbot component of the language
	 * 
	 * @param expression
	 *        the value to set for the argument expression
	 */
	public void setArgument(MetaExpression expression);

	/**
	 * @return The argument set by {@link Robot#setArgument(MetaExpression)}
	 */
	public MetaExpression getArgument();
}
