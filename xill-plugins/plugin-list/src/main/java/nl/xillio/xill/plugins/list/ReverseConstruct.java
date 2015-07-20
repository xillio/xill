package nl.xillio.xill.plugins.list;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map.Entry;

import nl.xillio.xill.api.components.ExpressionDataType;
import nl.xillio.xill.api.components.MetaExpression;
import nl.xillio.xill.api.construct.Argument;
import nl.xillio.xill.api.construct.Construct;
import nl.xillio.xill.api.construct.ConstructContext;
import nl.xillio.xill.api.construct.ConstructProcessor;

/**
 *
 *
 *
 *
 * @author Sander
 *
 */
public class ReverseConstruct extends Construct {

	@Override
	public String getName() {

	return "reverse";
	}

	@Override
	public ConstructProcessor prepareProcess(final ConstructContext context) {
	return new ConstructProcessor(ReverseConstruct::process, new Argument("list"), new Argument("recursive"));
	}

	private static MetaExpression process(final MetaExpression input, final MetaExpression recursiveVar) {

	boolean reverseRecursive = recursiveVar.getBooleanValue();

	MetaExpression result = NULL;
	
	if (input.getType() == LIST) {
		if (reverseRecursive) {
		result = processList(input);
		} else {
		List<MetaExpression> list = new ArrayList<>();
		for(MetaExpression m: (List<MetaExpression>)input.getValue()){
			list.add(m);
		}
		
		Collections.reverse(list);
		result = fromValue(list);
		}
	}
	else if (input.getType() == OBJECT) {
		if (reverseRecursive) {
		result = processObject(input);
		} else {
		result = reverseObject((LinkedHashMap<String, MetaExpression>) input.getValue());
		}
	}else {
		return input;
	}
	return result;

	}

	public static MetaExpression processList(final MetaExpression input) {
	List<MetaExpression> reversedList = new ArrayList<>();
	List<MetaExpression> inputList = (List<MetaExpression>) input.getValue();

	for (MetaExpression e : inputList) {
		if (e.getType() == ExpressionDataType.LIST) {
		reversedList.add(processList(e));
		} else if (e.getType() == OBJECT) {
		reversedList.add(processObject(e));
		} else {
		reversedList.add(e);
		}
	}
	Collections.reverse(reversedList);
	return fromValue(reversedList);
	}

	public static MetaExpression processObject(final MetaExpression input) {
	LinkedHashMap<String, MetaExpression> reversedObject = new LinkedHashMap<String, MetaExpression>();
	LinkedHashMap<String, MetaExpression> inputObject = (LinkedHashMap<String, MetaExpression>) input.getValue();

	
	for (Entry<String, MetaExpression> entry : inputObject.entrySet()) {
		if (entry.getValue().getType() == LIST) {
		reversedObject.put(entry.getKey(), processList(entry.getValue()));
		} else if (entry.getValue().getType() == OBJECT) {
		reversedObject.put(entry.getKey(), processObject(entry.getValue()));
		} else {
		reversedObject.put(entry.getKey(), entry.getValue());
		}
	}
	return reverseObject(reversedObject);
	}

	public static MetaExpression reverseObject(final LinkedHashMap<String, MetaExpression> input) {
	List<Entry<String, MetaExpression>> list = new ArrayList<>(input.entrySet());
	LinkedHashMap<String, MetaExpression> toReturn = new LinkedHashMap<String, MetaExpression>();
	for (int i = list.size() - 1; i >= 0; i--) {
		Entry<String, MetaExpression> entry = list.get(i);
		toReturn.put(entry.getKey(), entry.getValue());
	}
	return fromValue(toReturn);
	}
}
