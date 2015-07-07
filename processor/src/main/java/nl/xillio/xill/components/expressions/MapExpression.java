package nl.xillio.xill.components.expressions;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import nl.xillio.xill.api.Debugger;
import nl.xillio.xill.api.components.ExpressionBuilder;
import nl.xillio.xill.api.components.InstructionFlow;
import nl.xillio.xill.api.components.MetaExpression;
import nl.xillio.xill.api.components.Processable;
import nl.xillio.xill.api.errors.RobotRuntimeException;
import nl.xillio.xill.components.instructions.FunctionDeclaration;

/**
 * Call a function for every value in a collection
 */
public class MapExpression implements Processable, FunctionParameterExpression{

    private List<Processable> arguments;
    private FunctionDeclaration functionDeclaration;

    /**
     * Create a new {@link MapExpression}
     * @param arguments
     */
    public MapExpression(List<Processable> arguments) {
	this.arguments = arguments;
    }

    @SuppressWarnings("unchecked")
    @Override
    public InstructionFlow<MetaExpression> process(Debugger debugger) throws RobotRuntimeException {
	List<MetaExpression> results = new ArrayList<>();
	
	//Call the function for all arguments
	for(Processable argument : arguments) {
	    MetaExpression result = argument.process(debugger).get();
	    
	    switch(result.getType()) {
	    case ATOMIC:
		//Process the one argument
		results.add(functionDeclaration.run(debugger, Arrays.asList(result)).get());
		break;
	    case LIST:
		//Pass every argument
		for(MetaExpression expression : (List<MetaExpression>)result.getValue()) {
		    results.add(functionDeclaration.run(debugger, Arrays.asList(expression)).get());
		}
		break;
	    case OBJECT:
		//Pass every argument but with key
		for(Entry<String, MetaExpression> expression : ((Map<String, MetaExpression>)result.getValue()).entrySet()) {
		    results.add( functionDeclaration.run(debugger, Arrays.asList(ExpressionBuilder.fromValue(expression.getKey()), expression.getValue())).get());
		}
		break;
	    default:
		break;
	    
	    }
	}
	
	return InstructionFlow.doResume(ExpressionBuilder.fromValue(results));
    }

    @Override
    public Collection<Processable> getChildren() {
	return null;
    }

    /**
     * Set the function parameter
     * @param functionDeclaration
     */
    @Override
    public void setFunction(FunctionDeclaration functionDeclaration) {
	this.functionDeclaration = functionDeclaration;
    }

}
