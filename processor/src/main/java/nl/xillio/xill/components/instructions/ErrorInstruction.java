package nl.xillio.xill.components.instructions;

import nl.xillio.xill.CodePosition;
import nl.xillio.xill.api.Debugger;
import nl.xillio.xill.api.components.InstructionFlow;
import nl.xillio.xill.api.components.MetaExpression;
import nl.xillio.xill.api.components.Processable;
import nl.xillio.xill.api.errors.RobotRuntimeException;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.Arrays;
import java.util.Collection;
import java.util.Map;

/**
 * This {@link Instruction} represents the error mechanism
 */
public class ErrorInstruction extends CompoundInstruction {

    private static final Logger LOGGER = LogManager.getLogger();
    private final Map<String,InstructionSet> instructions;

    /**
     * Instantiate an {@link ErrorInstruction}
     *
     */
    public ErrorInstruction(final Map<String,InstructionSet> instructions) {
        this.instructions = instructions;

        instructions.get("DO").setParentInstruction(this);
    }

    @Override
    public void setHostInstruction(InstructionSet hostInstruction) {
        super.setHostInstruction(hostInstruction);
    }

    @Override
    public InstructionFlow<MetaExpression> process(final Debugger debugger) throws RobotRuntimeException {


        InstructionFlow<MetaExpression> Str = instructions.get("DO").process(debugger);

        if(instructions.containsKey("ERROR")) {
            return instructions.get("ERROR").process(debugger);
        }

        if(instructions.containsKey("SUCCES")) {
            return instructions.get("SUCCES").process(debugger);
        }

        
        if(instructions.containsKey("FINALLY")){
            return instructions.get("FINALLY").process(debugger);
        }

        return InstructionFlow.doResume();
    }

    @Override
    public void setPosition(CodePosition position) {
        super.setPosition(position);
    }

    @Override
    public Collection<Processable> getChildren() {
        return Arrays.asList(instructions.get("DO"));
    }
}
