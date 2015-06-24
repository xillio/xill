package nl.xillio.xill.api;

import java.io.IOException;
import java.util.Collection;
import java.util.List;

import nl.xillio.xill.api.components.Robot;
import nl.xillio.xill.api.components.RobotID;
import nl.xillio.xill.api.errors.XillParsingException;

/**
 * This interface represents the main processing object
 */
public interface XillProcessor {
	/**
	 * Compiles the code in the file used to instantiate this {@link XillProcessor}
	 *
	 * @return A list of {@link Issue} with the code. (Does not contain errors)
	 * @throws XS_ScriptException
	 *         When the code was not compiled correctly
	 * @throws IOException
	 * @throws SyntaxError
	 *         When there is an error in the code
	 * @throws XillParsingException
	 */
	public List<Issue> compile() throws IOException, XillParsingException;

	/**
	 * Run {@link XillProcessor#compile()} first
	 * 
	 * @return the compiled robot.
	 */
	public Robot getRobot();

	/**
	 * @return the RobotID
	 */
	public RobotID getRobotID();

	/**
	 * @return The debugger for this processor
	 */
	public Debugger getDebugger();
	
	/**
	 * @return the names of all available packages
	 */
	public Collection<String> listPackages();
}
