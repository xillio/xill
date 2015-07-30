package nl.xillio.xill.plugins.list.services.sort;

import java.util.Collection;
import java.util.Comparator;
import java.util.IdentityHashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.stream.Collectors;

/**
 * This is the main implementation of {@link Sort}
 *
 * @author Sander Visser
 */
public class SortImpl implements Comparator<Object>, Sort {


	@Override
	public Object asSorted(final Object input, final boolean recursive, final boolean onKeys) {
		return asSorted(input, recursive, onKeys, new IdentityHashMap<>());
	}

	@SuppressWarnings("unchecked")
	private Object asSorted(final Object input, final boolean recursive, final boolean onKeys, final IdentityHashMap<Object, Object> results) {
		if (results.containsKey(input)) {
			return results.get(input);
		}

		if (input instanceof List) {
			List<?> list = (List<?>) input;
			List<Object> sorted = list.stream().sorted(this).collect(Collectors.toList());
			results.put(list, sorted);

			// Sort recursive
			if (recursive) {
				for (int i = 0; i < sorted.size(); i++) {
					Object child = sorted.get(i);
					sorted.set(i, asSorted(child, recursive, onKeys, results));
				}
			}

			return sorted;
		} else if (input instanceof Map) {
			// Sort the map by extracting single entries and sorting them either by key or
			Map.Entry<String, Object>[] sortedEntries = ((Map<String, Object>) input)
				.entrySet()
				.stream()
				.sorted((a, b) -> {
					if (onKeys) {
						return a.getKey().compareTo(b.getKey());
					}
					return compare(a, b);
				})
				.toArray(i -> new Map.Entry[i]);

			Map<String, Object> map = new LinkedHashMap<>();
			for (Entry<String, Object> entry : sortedEntries) {
				map.put(entry.getKey(), entry.getValue());
			}

			// Sort recursive
			if (recursive) {
				for (String key : map.keySet()) {
					Object child = map.get(key);
					map.put(key, asSorted(child, recursive, onKeys, results));
				}
			}

			return map;
		}

		return input;
	}

	@SuppressWarnings("unchecked")
	@Override
	public int compare(final Object objectA, final Object objectB) {
		int priorityA = getPriorityIndex(objectA);
		int priorityB = getPriorityIndex(objectB);

		if (priorityA != priorityB) {
			return priorityA - priorityB;
		}

		// These objects are of the same type.
		if (objectA.equals(objectB)) {
			return 0;
		}

		if (objectA instanceof Collection) {
			return ((Collection<?>) objectA).size() - ((Collection<?>) objectB).size();
		}

		if (objectA instanceof Number) {
			Number numberA = (Number) objectA;
			Number numberB = (Number) objectB;

			return Double.compare(numberA.doubleValue(), numberB.doubleValue());
		}

		if (objectA instanceof Boolean) {
			boolean booleanA = (boolean) objectA;
			boolean booleanB = (boolean) objectB;
			return Boolean.compare(booleanA, booleanB);
		}

		if (objectA instanceof Entry) {
			return ((Entry<String, Object>) objectA).getValue().toString().compareTo(((Entry<String, Object>) objectB).getValue().toString());
		}
		// This is probably a string
		return objectA.toString().compareTo(objectB.toString());
	}

	private static int getPriorityIndex(final Object object) {
		if (object instanceof List) {
			return 0;
		}

		if (object instanceof Map) {
			return 1;
		}

		if (object instanceof Boolean) {
			return 2;
		}

		if (object instanceof Number) {
			return 3;
		}

		if (object != null) {
			return 4;
		}

		return 5;
	}
}
