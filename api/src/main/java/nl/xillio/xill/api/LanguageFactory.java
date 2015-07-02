package nl.xillio.xill.api;

import nl.xillio.xill.api.components.Robot;
import nl.xillio.xill.api.components.RobotID;
import nl.xillio.xill.api.errors.XillParsingException;

/**
 * This interface represents a factory that can build a program tree.
 *
 * @param <T>
 */
public interface LanguageFactory<T> {
	/**
	 * Process a token into a {@link Robot} which can be processed
	 *
	 * @param token
	 *        The token that should be processed
	 * @param robotID
	 * @param libraries
	 * @throws XillParsingException
	 */	
	public void parse(final T token, final RobotID robotID) throws XillParsingException;
	
	/**
	 * Finish compiling
	 * @throws XillParsingException
	 */
	public void compile() throws XillParsingException;
	
	/**
	 * Get a compiled robot
	 * @param token
	 * @return the compiled robot for that token or null if none was found
	 */
	public Robot getRobot(final T token);
}
