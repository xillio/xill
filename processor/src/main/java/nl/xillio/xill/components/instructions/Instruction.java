package nl.xillio.xill.components.instructions;

import nl.xillio.xill.CodePosition;
import nl.xillio.xill.api.components.RobotID;

/**
 * This is the base interface for all instructions. An instruction generally represents code that would qualify as a minimal syntactically correct program.
 */
public abstract class Instruction implements nl.xillio.xill.api.components.Instruction {

	private CodePosition position;

	@Override
	public RobotID getRobotID() {
		return position.getRobotID();
	}

	@Override
	public int getLineNumber() {
		return position.getLineNumber();
	}

	/**
	 * Set the code position of this instruction.
	 * <b>Note!</b> This value can only be set once!
	 *
	 * @param position
	 */
	public void setPosition(final CodePosition position) {
		if (this.position != null) {
			return;
		}

		this.position = position;
	}

	protected CodePosition getPosition() {
		return position;
	}

	@Override
	public String toString() {
		String path = getRobotID().getProjectPath().toURI().relativize(getRobotID().getPath().toURI()).getPath();

		return path + ":" + getLineNumber() + " > " + getClass().getSimpleName();
	}

	/**
	 * @return true if debugging should be prevented
	 */
	public boolean preventDebugging() {
		return false;
	}

}
