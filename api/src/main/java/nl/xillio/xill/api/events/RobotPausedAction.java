package nl.xillio.xill.api.events;

import nl.xillio.xill.api.components.Instruction;
import nl.xillio.xill.api.components.RobotID;

/**
 * This class contains all information on what happened when a robot paused
 */
public class RobotPausedAction {
    private final Instruction instruction;

    /**
     * Instantiate the action and pull all data from an instruction
     *
     * @param instruction the instruction to pull the data from
     */
    public RobotPausedAction(final Instruction instruction) {
        this.instruction = instruction;
    }

    /**
     * @return The line number the robot paused on
     */
    public int getLineNumber() {
        return instruction.getLineNumber();
    }

    /**
     * @return The robotID that caused the pause
     */
    public RobotID getRobotID() {
        return instruction.getRobotID();
    }
}
