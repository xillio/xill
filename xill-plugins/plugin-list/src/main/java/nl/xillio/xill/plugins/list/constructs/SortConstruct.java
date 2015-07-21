package nl.xillio.xill.plugins.list.constructs;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import nl.xillio.xill.api.components.MetaExpression;
import nl.xillio.xill.api.construct.Argument;
import nl.xillio.xill.api.construct.Construct;
import nl.xillio.xill.api.construct.ConstructContext;
import nl.xillio.xill.api.construct.ConstructProcessor;
import nl.xillio.xill.plugins.list.util.Reverse;

/**
 *
 *
 *
 *
 * @author Sander
 *
 */
public class SortConstruct extends Construct {

	@Override
	public String getName() {

	return "sort";
	}

	@Override
	public ConstructProcessor prepareProcess(final ConstructContext context) {
	return new ConstructProcessor(SortConstruct::process, new Argument("list"), new Argument("keys"), new Argument("sub"));
	}

	private static MetaExpression process(final MetaExpression inputList, final MetaExpression keys, final MetaExpression recursive) {

	boolean sortKeys = keys.getBooleanValue();
	boolean sortRecursive = recursive.getBooleanValue();

	if (inputList == NULL || inputList == emptyList()) {
		return emptyList();
	}

	if (inputList.getType() == LIST) {
		return processList(inputList, sortKeys, sortRecursive);
	} else if (inputList.getType() == OBJECT) {
		return processObject(inputList, sortKeys, sortRecursive);
	}
	return NULL;
	}

	/**
	 * sorting by key
	 * stack is: LIST -> explicit keys -> generated keys
	 * generated keys aren't visible -> the result may seem odd
	 *
	 * sorting by value
	 * stack is: LIST -> NUMBER -> STRING
	 * anything except LIST|NUMBER is STRING
	 *
	 * @param list
	 *        the list to sord
	 * @param sortRecursive
	 *        whether to sort recursively
	 * @param sortKeys
	 *        whether to sort by keys
	 * @return a sorted list
	 */
	private static MetaExpression processList(final MetaExpression list, final boolean sortKeys, final boolean recursive) {

	List<MetaExpression> input = (List<MetaExpression>) list.getValue();
	List<MetaExpression> result = new ArrayList<>();

	List<String> stringElement = new ArrayList<>();
	List<MetaExpression> numberElement = new ArrayList<>();
	List<MetaExpression> listElement = new ArrayList<>();
	List<MetaExpression> objectElement = new ArrayList<>();
	// make lists of different element types
	for (MetaExpression m : input) {
		if (m.getType() == OBJECT) {
		MetaExpression sortedSubObj = NULL;
		if (recursive) {
			sortedSubObj = processObject(m, sortKeys,recursive);
			objectElement.add(sortedSubObj);
		} else {
			objectElement.add(m);
		}
		} else if (m.getType() == LIST) {
		MetaExpression sortedSubList = NULL;
		if (recursive) {
			sortedSubList = processList(m, sortKeys,recursive);
			listElement.add(sortedSubList);
		} else {
			listElement.add(m);
		}
		} else {
		if (!m.toString().startsWith("\"")) {
			numberElement.add(m);
		} else {
			stringElement.add(m.getStringValue());
		}
		}
	}

	// sort and combine the lists
	if (!objectElement.isEmpty()) {
		result.addAll(objectElement);
	}
	if (!listElement.isEmpty()) {
		result.addAll(listElement);
	}
	if (!numberElement.isEmpty()) {
		result.addAll(sortNumber(numberElement));
	}
	if (!stringElement.isEmpty()) {
		result.addAll(sortString(stringElement));
	}

	return fromValue(result);
	}

	public static MetaExpression processObject(final MetaExpression input, final boolean sortKeys, final boolean recursive) {
	LinkedHashMap<String, MetaExpression> result = new LinkedHashMap<String, MetaExpression>();
	LinkedHashMap<String, MetaExpression> inputObject = (LinkedHashMap<String, MetaExpression>) input.getValue();

	LinkedHashMap<String, MetaExpression> listObjElement = new LinkedHashMap<String, MetaExpression>();
	LinkedHashMap<String, MetaExpression> objObjElement = new LinkedHashMap<String, MetaExpression>();
	LinkedHashMap<String, MetaExpression> stringObjElement = new LinkedHashMap<String, MetaExpression>();
	LinkedHashMap<String, MetaExpression> numberObjElement = new LinkedHashMap<String, MetaExpression>();

	for (Entry<String, MetaExpression> entry : inputObject.entrySet()) {
		if (entry.getValue().getType() == LIST) {
		if (recursive) {
			listObjElement.put(entry.getKey(), processList(entry.getValue(), sortKeys,recursive));
		} else {
			listObjElement.put(entry.getKey(), entry.getValue());
		}
		} else if (entry.getValue().getType() == OBJECT) {
		if (recursive) {
			objObjElement.put(entry.getKey(), processObject(entry.getValue(), sortKeys,recursive));
		} else {
			objObjElement.put(entry.getKey(), entry.getValue());
		}
		} else {
		if (extractValue(entry.getValue()) instanceof Double) {
			numberObjElement.put(entry.getKey(), entry.getValue());
		} else {
			stringObjElement.put(entry.getKey(), entry.getValue());
		}
		}
	}

	if (!objObjElement.isEmpty()) {
		result.putAll(objObjElement);
	}
	if (!listObjElement.isEmpty()) {
		result.putAll(listObjElement);
	}
	if (!numberObjElement.isEmpty()) {
		if (!sortKeys) {
		result.putAll(sortObj(numberObjElement));
		} else {
		result.putAll(sortKeys(numberObjElement));
		}
	}
	if (!stringObjElement.isEmpty()) {
		if (!sortKeys) {
		result.putAll(sortObj(stringObjElement));
		} else {
		result.putAll(sortKeys(stringObjElement));
		}
	}
	return fromValue(result);
	}

	private static List<MetaExpression> sortNumber(final List<MetaExpression> list) {

	Collections.sort(list, (a1, a2) -> {
		double v1 = a1.getNumberValue().doubleValue();
		double v2 = a2.getNumberValue().doubleValue();

		return Double.compare(v1, v2);
	});

	return list;
	}

	private static List<MetaExpression> sortString(final List<String> list) {

	Collections.sort(list, String.CASE_INSENSITIVE_ORDER);
	List<MetaExpression> result = new ArrayList<MetaExpression>();
	for (String s : list) {
		result.add(fromValue(s));
	}
	return result;

	}

	private static LinkedHashMap<String, MetaExpression> sortObj(final LinkedHashMap<String, MetaExpression> list) {
	List<Map.Entry<String, MetaExpression>> input = new LinkedList<>(list.entrySet());
	LinkedHashMap<String, MetaExpression> result = new LinkedHashMap<String, MetaExpression>();

	Collections.sort(input, (a1, a2) -> {
		if (extractValue(a1.getValue()) instanceof Double) {
		double v1 = a1.getValue().getNumberValue().doubleValue();
		double v2 = a2.getValue().getNumberValue().doubleValue();
		return Double.compare(v1, v2);
		} else {
		String s1 = a1.getValue().getStringValue();
		String s2 = a2.getValue().getStringValue();
		return s1.compareTo(s2);
		}
	});
	for (Map.Entry<String, MetaExpression> entry : input) {
		result.put(entry.getKey(), entry.getValue());
	}
	return result;

	}

	private static LinkedHashMap<String, MetaExpression> sortKeys(final LinkedHashMap<String, MetaExpression> list) {
	List<Map.Entry<String, MetaExpression>> input = new LinkedList<>(list.entrySet());
	LinkedHashMap<String, MetaExpression> result = new LinkedHashMap<String, MetaExpression>();

	Collections.sort(input, (a1, a2) -> {
		String s1 = a1.getKey();
		String s2 = a2.getKey();
		return s1.compareTo(s2);

	});

	for (Map.Entry<String, MetaExpression> entry : input) {
		result.put(entry.getKey(), entry.getValue());
	}
	return result;

	}
}
