package nl.xillio.xill.api.components;

/**
 * This interface represents an Instruction in Xill language.
 */
public interface Instruction extends Processable, AutoCloseable {

    /**
     * @return the starting line number of this instruction
     */
    public int getLineNumber();

    /**
     * @return the robotID of the robot that holds this instruction
     */
    public RobotID getRobotID();

}
