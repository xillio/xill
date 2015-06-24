package nl.xillio.xill.api.components;

/**
 * This interface represents an Instruction in Xill code
 */
public interface Instruction extends Processable, AutoCloseable {

	/**
	 * @return The starting line number of this instruction
	 */
	public int getLineNumber();

	/**
	 * @return The robotID of this instruction
	 */
	public RobotID getRobotID();

}
