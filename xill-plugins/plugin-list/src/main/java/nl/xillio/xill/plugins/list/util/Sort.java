package nl.xillio.xill.plugins.list.util;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

/**
 * @author Sander
 *
 */
public class Sort {

	/**
	 *
	 */

	List<Object> disc = new ArrayList<>();

	/**
	 * @param input the list
	 * @param recursive whether it should sort lists inside the list
	 * @param onKeys whether it should sort by key
	 * @return the sorted list
	 */
	public Object doSorting(final Object input, final boolean recursive, final boolean onKeys) {
		disc.add(input);
		if (input instanceof List<?>) {
			return doList(input, recursive, onKeys);
		}
		return doObject(input, recursive, onKeys);
	}
	/**
	 * sorts a list.
	 *
	 */
	private Object doList(final Object input, final boolean recursive, final boolean onKeys) {
		List<String> stringElement = new ArrayList<>();
		List<Object> numberElement = new ArrayList<>();
		List<Object> listElement = new ArrayList<>();
		List<Object> mapElement = new ArrayList<>();

		// disc.add(input);
		for (Object o : (List<Object>) input) {
			if (o instanceof List<?>) {
				if (recursive && !checkCircular(o)) {
					listElement.add(doList(o, recursive, onKeys));
				} else {
					listElement.add(o);
				}
			} else if (o instanceof Map<?, ?>) {
				if (recursive && !checkCircular(o)) {
					mapElement.add(doObject(o, recursive, onKeys));
				} else {
					mapElement.add(o);
				}
			} else {
				if (o instanceof Integer || o instanceof Double) {
					numberElement.add(((Number) o).doubleValue());
				} else if (o instanceof String) {
					stringElement.add(o.toString());
				}
			}
		}

		clearInput(input);

		if (!mapElement.isEmpty()) {
			((List<Object>) input).addAll(mapElement);
		}
		if (!listElement.isEmpty()) {
			((List<Object>) input).addAll(listElement);
		}
		if (!numberElement.isEmpty()) {
			((List<Object>) input).addAll(sortNumber(numberElement));
		}
		if (!stringElement.isEmpty()) {
			Collections.sort(stringElement, String.CASE_INSENSITIVE_ORDER);
			((List<Object>) input).addAll(stringElement);
		}

		return input;
	}
	/**
	 * sorts a map
	 *
	 */
	@SuppressWarnings("unchecked")
	private Object doObject(final Object input, final boolean recursive, final boolean onKeys) {
		LinkedHashMap<String, Object> objList = new LinkedHashMap<String, Object>();
		LinkedHashMap<String, Object> listList = new LinkedHashMap<String, Object>();
		LinkedHashMap<String, Object> numberList = new LinkedHashMap<String, Object>();
		LinkedHashMap<String, Object> stringList = new LinkedHashMap<String, Object>();
		// disc.add(input);
		for (Entry<String, Object> entry : ((Map<String, Object>) input).entrySet()) {
			if (entry.getValue() instanceof Map<?, ?>) {
				if (recursive && !checkCircular(entry.getValue())) {
					entry.setValue(doObject(entry.getValue(), recursive, onKeys));
				}
				objList.put(entry.getKey(), entry.getValue());
			} else if (entry.getValue() instanceof List<?>) {
				if (recursive && !checkCircular(entry.getValue())) {
					entry.setValue(doList(entry.getValue(), recursive, onKeys));
				}
				listList.put(entry.getKey(), entry.getValue());
			} else if (entry.getValue() instanceof Integer || entry.getValue() instanceof Double) {
				numberList.put(entry.getKey(), entry.getValue());
			} else if (entry.getValue() instanceof String) {
				stringList.put(entry.getKey(), entry.getValue());
			}
		}

		clearInput(input);

		((Map<String, Object>) input).putAll(objList);
		((Map<String, Object>) input).putAll(listList);
		if (!onKeys) {
			((Map<String, Object>) input).putAll(sortObj(numberList));
			((Map<String, Object>) input).putAll(sortObj(stringList));
		} else {
			((Map<String, Object>) input).putAll(numberList);
			((Map<String, Object>) input).putAll(stringList);
			sortKeys((LinkedHashMap<String, Object>) input);
		}

		return input;
	}

	/**
	 * returns true if there is a circular reference othrwise false.
	 * 
	 *
	 */
	private boolean checkCircular(final Object input) {
		if (input instanceof List<?>) {
			for (Object e : disc) {

				if (e == input) {
					return true; // there is a CR.
				}
			}
			return false;
		} else if (input instanceof Map<?, ?>) {
			for (Object e : disc) {
				{
					if (e instanceof Map<?, ?>) {
						if (e.equals(input)) {
							return true;
						}
					}
					return false;
				}
			}
		}
		return false;
	}

	/**
	 * clear an list or map
	 *
	 */
	@SuppressWarnings("unchecked")
	private static void clearInput(Object input) {
		if (input instanceof List<?>) {
			((List<Object>) input).clear();
		} else if (input instanceof Map<?, ?>) {
			((Map<String, Object>) input).clear();
		} else {
			input = null;
		}
	}

	
	/**
	 * sorts a list of numbers
	 *
	 */
	private static List<Object> sortNumber(final List<Object> list) {
		Collections.sort(list, (a1, a2) -> {
			double v1 = (double) a1;
			double v2 = (double) a2;
			return Double.compare(v1, v2);
		});
		return list;
	}

	
	/**
	 * sorts a LinkedhashMap by its values
	 *
	 */
	private static LinkedHashMap<String, Object> sortObj(final LinkedHashMap<String, Object> list) {
		List<Map.Entry<String, Object>> input = new LinkedList<>(list.entrySet());

		Collections.sort(input, (a1, a2) -> {
			if (a1.getValue() instanceof Double) {
				double v1 = (double) a1.getValue();
				double v2 = (double) a2.getValue();
				return Double.compare(v1, v2);
			}
			String s1 = a1.getValue().toString();
			String s2 = a2.getValue().toString();
			return s1.compareTo(s2);
		});

		list.clear();

		for (Map.Entry<String, Object> entry : input) {
			list.put(entry.getKey(), entry.getValue());
		}
		return list;
	}
	/**
	 * sorts a LinkedhashMap by its key values
	 *
	 */
	private static LinkedHashMap<String, Object> sortKeys(final LinkedHashMap<String, Object> list) {
		List<Map.Entry<String, Object>> input = new LinkedList<>(list.entrySet());

		Collections.sort(input, (a1, a2) -> {
			String s1 = a1.getKey();
			String s2 = a2.getKey();
			return s1.compareTo(s2);
		});

		list.clear();

		for (Map.Entry<String, Object> entry : input) {
			list.put(entry.getKey(), entry.getValue());
		}
		return list;

	}
}
