package nl.xillio.xill.components.operators;

import nl.xillio.xill.api.Debugger;
import nl.xillio.xill.api.components.AtomicExpression;
import nl.xillio.xill.api.components.InstructionFlow;
import nl.xillio.xill.api.components.MetaExpression;
import nl.xillio.xill.api.components.Processable;
import nl.xillio.xill.api.errors.RobotRuntimeException;

import java.util.Arrays;
import java.util.Collection;

/**
 * This class represents the || operator
 */
public class Negate implements Processable {

    private final Processable value;

    /**
     * @param value
     */
    public Negate(final Processable value) {
        this.value = value;

    }

    @Override
    public InstructionFlow<MetaExpression> process(final Debugger debugger) throws RobotRuntimeException {
        MetaExpression processedValue = value.process(debugger).get();
        processedValue.registerReference();

        boolean result = !processedValue.getBooleanValue();

        processedValue.releaseReference();
        return InstructionFlow.doResume(new AtomicExpression(result));
    }

    @Override
    public Collection<Processable> getChildren() {
        return Arrays.asList(value);
    }

}
