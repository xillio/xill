package nl.xillio.xill.api;

import nl.xillio.xill.api.components.Robot;
import nl.xillio.xill.api.components.RobotID;
import nl.xillio.xill.api.errors.XillParsingException;

import java.io.IOException;
import java.util.Collection;
import java.util.List;
import java.util.Map;

/**
 * This interface represents the main processing object.
 */
public interface XillProcessor {
    /**
     * Compiles the code in the file used to instantiate this processor.
     *
     * @return A list of {@link Issue} with the code. (Does not contain errors)
     * @throws IOException          When the file could not be read
     * @throws XillParsingException When the code was not compiled correctly
     */
    List<Issue> compile() throws IOException, XillParsingException;

    /**
     * Compiles the code in the file used to instantiate this processor.
     * This robot wil run as a subrobot of another robot. This means that whenever any
     * component requests the root robot it will receive the parent.
     *
     * @param parentRobotId the parent of this robot
     * @return A list of {@link Issue} with the code. (Does not contain errors)
     * @throws IOException          When the file could not be read
     * @throws XillParsingException When the code was not compiled correctly
     */
    List<Issue> compileAsSubRobot(RobotID parentRobotId) throws IOException, XillParsingException;

    /**
     * Tests a robot for syntax errors.
     *
     * @return a list of issues
     */
    List<Issue> validate();

    /**
     * Returns the compiled robot, AFTER you have run {@link XillProcessor#compile()}.
     *
     * @return the compiled robot
     */
    Robot getRobot();

    /**
     * @return the ID of this robot.
     */
    RobotID getRobotID();

    /**
     * @return the debugger for this processor
     */
    Debugger getDebugger();

    /**
     * @return the names of all available packages
     */
    Collection<String> listPackages();

    /**
     * @return all reserved keywords
     */
    String[] getReservedKeywords();

    Map<String, List<String>> getCompletions(String currentLine, String prefix, int column, int row);
}
