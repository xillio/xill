package nl.xillio.xill.plugins.example;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import nl.xillio.xill.api.components.ExpressionBuilder;
import nl.xillio.xill.api.components.MetaExpression;
import nl.xillio.xill.api.construct.Argument;
import nl.xillio.xill.api.construct.Construct;
import nl.xillio.xill.api.construct.ConstructContext;
import nl.xillio.xill.api.construct.ConstructProcessor;
import nl.xillio.xill.api.errors.NotImplementedException;

/**
 * This construct takes a value and returns a deep copy of it
 *
 */
public class CopyConstruct implements Construct {

	@Override
	public String getName() {
	return "copy";
	}

	@Override
	public ConstructProcessor prepareProcess(final ConstructContext context) {
	/*
	 * This construct takes 1 argument so we need to define this.
	 * Also we now have to make sure the process method takes 1 argument of type MetaExpression
	 */
	return new ConstructProcessor(CopyConstruct::process, new Argument("input"));
	}

	/**
	 * As this construct is more complex than the {@link LifeConstuct} and requires multiple lines of code
	 * we decided to extract the actual processing functionality to a static method (to enforce no side effects) that
	 * will be passed as a parameter in the {@link ConstructProcessor}
	 *
	 * @param input
	 * @return
	 */
	private static MetaExpression process(final MetaExpression input) {
	switch (input.getType()) {

		// This already is an atomic value so we return it
		case ATOMIC:
		return input;
		
		//This expression is a list so we need to construct a new list for the new expression
		case LIST:
		// Build a new list by recursively calling the process method
		List<MetaExpression> listExpression = new ArrayList<>();

		// We can suppress this warning because we are checking using getType()
		@SuppressWarnings("unchecked")
		List<MetaExpression> listValue = (List<MetaExpression>) input.getValue();

		// Add all the new elements to the list after processing
		listValue.forEach(val -> listExpression.add(process(val)));

		// Build the expression
		return ExpressionBuilder.fromValue(listExpression);
		
		
		//This expression is an Object so we need to construct a new Map for the new expression
		case OBJECT:
		// Build a new map by recursively calling the process method
		Map<String, MetaExpression> objectExpression = new HashMap<>();

		// We can suppress this warning because we are checking using getType()
		@SuppressWarnings("unchecked")
		Map<String, MetaExpression> objectValue = (Map<String, MetaExpression>) input.getValue();

		// Add all the new elements to the map after processing
		objectValue.forEach((key, value) -> {
			objectExpression.put(key, process(value));
		});

		// Build the expression
		return ExpressionBuilder.fromValue(objectExpression);
		default:
			throw new NotImplementedException("This method has not been implemented for type " + input.getType());

	}
	}
}
