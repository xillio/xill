package nl.xillio.xill.components.expressions;

import nl.xillio.xill.api.Debugger;
import nl.xillio.xill.api.components.MetaExpression;
import nl.xillio.xill.api.construct.ExpressionBuilderHelper;
import nl.xillio.xill.api.errors.RobotRuntimeException;
import nl.xillio.xill.components.instructions.FunctionDeclaration;

import java.util.*;

/**
 * @author Pieter Soels
 *
 * This class is for abstracting methods from the map- and filterexpression classes.
 */
public class MapFilterHandler {
    protected FunctionDeclaration functionDeclaration;

    public MapFilterHandler() { }

    /**
     * Instruction set for the process of atomics.
     * Run the function and return the result.
     * When function has one argument give the meta expression.
     * When the function has two arguments give an key (index) as well.
     */
    protected List<MetaExpression> atomicHandler(MetaExpression input, Debugger debugger){
        List<MetaExpression> result = new ArrayList<>(1);

        if (functionDeclaration.getParametersSize() == 1){
            result.add(functionDeclaration.run(debugger, Collections.singletonList(input)).get());
        } else if (functionDeclaration.getParametersSize() == 2){
            result.add(functionDeclaration.run(debugger,
                    Arrays.asList(ExpressionBuilderHelper.fromValue(0), input)).get());
        } else {
            throw new RobotRuntimeException("The given function does not accept one or two arguments.");
        }

        return result;
    }

    /**
     * Instruction set for the process of lists.
     * Run the function on each element and return the result.
     * When function has one argument give the meta expression.
     * When the function has two arguments give an key (index) as well.
     */
    @SuppressWarnings("unchecked")
    protected List<MetaExpression> listHandler(MetaExpression input, Debugger debugger){
        List<MetaExpression> listResults = new ArrayList<>(input.getNumberValue().intValue());

        if (functionDeclaration.getParametersSize() == 1) {
            for (MetaExpression expression : (List<MetaExpression>) input.getValue()) {
                listResults.add(functionDeclaration.run(debugger, Collections.singletonList(expression)).get());
            }
        } else if (functionDeclaration.getParametersSize() == 2) {
            List<MetaExpression> expressions = (List<MetaExpression>)input.getValue();
            for (int i = 0; i < expressions.size() ; i++) {
                listResults.add(functionDeclaration.run(debugger,
                        Arrays.asList(ExpressionBuilderHelper.fromValue(i), expressions.get(i))).get());
            }
        } else {
            throw new RobotRuntimeException("The given function does not accept one or two arguments.");
        }

        return listResults;
    }

    /**
     * Instruction set for the process of objects.
     * Run the functions on each element and return the result.
     * When function has one argument give the value of the object
     * When the function has two arguments give the key as well.
     */
    @SuppressWarnings("unchecked")
    protected LinkedHashMap<String, MetaExpression> objectHandler(MetaExpression input, Debugger debugger){
        LinkedHashMap<String, MetaExpression> objectResults = new LinkedHashMap<>(input.getNumberValue().intValue());

        if (functionDeclaration.getParametersSize() == 1) {
            for (Map.Entry<String, MetaExpression> expression : ((Map<String, MetaExpression>) input.getValue()).entrySet()) {
                objectResults.put(expression.getKey(), functionDeclaration.run(debugger, Collections.singletonList(expression.getValue())).get());
            }
        } else if (functionDeclaration.getParametersSize() == 2) {
            for (Map.Entry<String, MetaExpression> expression : ((Map<String, MetaExpression>) input.getValue()).entrySet()) {
                objectResults.put(expression.getKey(), functionDeclaration.run(debugger,
                        Arrays.asList(ExpressionBuilderHelper.fromValue(expression.getKey()), expression.getValue())).get());
            }
        } else {
            throw new RobotRuntimeException("The given function does not accept one or two arguments.");
        }
        return objectResults;
    }
}
