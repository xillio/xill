package nl.xillio.xill.components.instructions;


/**
 * This is a marker interface that disables step-over and runs a step-in instead in the debugger.
 *
 * @author Thomas Biesaart
 */
public abstract class CompoundInstruction extends Instruction {

    @Override
    public boolean preventDebugging() {
        return true;
    }
}
