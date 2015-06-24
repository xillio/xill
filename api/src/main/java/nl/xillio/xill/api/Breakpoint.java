package nl.xillio.xill.api;

import nl.xillio.xill.api.components.Instruction;
import nl.xillio.xill.api.components.RobotID;

/**
 * This class represents a breakpoint in code
 */
public class Breakpoint {
	private final RobotID robotID;
	private final int lineNumber;

	/**
	 * @param robotID
	 * @param lineNumber
	 */
	public Breakpoint(final RobotID robotID, final int lineNumber) {
		this.robotID = robotID;
		this.lineNumber = lineNumber;
	}

	/**
	 * Returns true if the breakpoint was hit
	 *
	 * @param previous
	 * @param next
	 * @return true if the breakpoint was hit
	 */
	public boolean matches(final Instruction previous, final Instruction next) {
		return next.getRobotID() == robotID &&
				next.getLineNumber() == lineNumber &&
				(previous == null || 
						previous.getRobotID() != robotID ||
						previous.getLineNumber() != lineNumber
				);
	}
}
