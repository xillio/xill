package nl.xillio.xill.api.components;

import nl.xillio.xill.api.Debugger;
import nl.xillio.xill.api.errors.RobotRuntimeException;

import java.util.Collection;

/**
 * This interface represents an object that can be processed.
 */
public interface Processable {

    /**
     * Processes this object.
     *
     * @param debugger The debugger that should be used when processing this
     * @return The return value is there is one
     * @throws RobotRuntimeException When processing went wrong
     */
    InstructionFlow<MetaExpression> process(Debugger debugger);

    /**
     * Collects all {@link Processable} used by this one. This is used to search through program trees.
     *
     * @return all Children
     */
    Collection<Processable> getChildren();
}
