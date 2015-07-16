package nl.xillio.xill.api;

import nl.xillio.xill.api.components.Robot;
import nl.xillio.xill.api.components.RobotID;
import nl.xillio.xill.api.errors.XillParsingException;

/**
 * This interface represents a factory that can build a program tree.
 *
 * @param <T> the input token for this factory
 */
public interface LanguageFactory<T> {
	/**
	 * Process a token into a {@link Robot} which can be processed
	 *
	 * @param token
	 *        The token that should be processed
	 * @param robotID the id that should be set for the robot
	 * @throws XillParsingException when the robot cannot be parsed
	 */	
	public void parse(final T token, final RobotID robotID) throws XillParsingException;
	
	/**
	 * Finish compiling
	 * @throws XillParsingException when the robot cannot be compiled
	 */
	public void compile() throws XillParsingException;
	
	/**
	 * Get a compiled robot
	 * @param token the token used to compile the robot
	 * @return the compiled robot for that token or null if none was found
	 */
	public Robot getRobot(final T token);
}
