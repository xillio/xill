package nl.xillio.xill.api;

import java.util.List;

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
	 * @return The processed {@link Robot}
	 * @throws XillParsingException
	 */
	public Robot parse(final T token, final RobotID robotID, final List<Robot> libraries) throws XillParsingException;
}
