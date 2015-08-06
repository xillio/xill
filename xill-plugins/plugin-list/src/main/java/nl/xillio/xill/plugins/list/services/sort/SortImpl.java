package nl.xillio.xill.plugins.list.services.sort;


import java.util.Collection;
import java.util.Comparator;
import java.util.IdentityHashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.stream.Collectors;
import com.google.inject.Singleton;

/**
 * This is the main implementation of {@link Sort}
 *
 * @author Sander Visser
 */
@Singleton
public class SortImpl implements Sort {

	@Override
	public Object asSorted(final Object input, final boolean recursive, final boolean onKeys, final boolean reverse) {
		return asSorted(input, recursive, onKeys, reverse, new IdentityHashMap<>());
	}

	@SuppressWarnings("unchecked")
	private Object asSorted(final Object input, final boolean recursive, final boolean onKeys, final boolean reverse, final IdentityHashMap<Object, Object> results) {
		if (results.containsKey(input)) {
			return results.get(input);
		}
		Sorter sorter = reverse ? Sorter.REVERSE : Sorter.NORMAL;

		if (input instanceof List) {
			List<?> list = (List<?>) input;
			List<Object> sorted = list.stream().sorted(sorter).collect(Collectors.toList());
			results.put(list, sorted);

			// Sort recursive
			if (recursive) {
				for (int i = 0; i < sorted.size(); i++) {
					Object child = sorted.get(i);
					sorted.set(i, asSorted(child, recursive, onKeys, reverse, results));
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
					return sorter.compare(a.getValue(), b.getValue());
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
					map.put(key, asSorted(child, recursive, onKeys, reverse, results));
				}
			}

			return map;
		}

		return input;
	}
	//returns the priority of the type of the input. 
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

	private static class Sorter implements Comparator<Object> {
		public static final Sorter NORMAL = new Sorter(false);
		public static final Sorter REVERSE = new Sorter(true);
		private final boolean reverse;

		private Sorter(final boolean reverseOrder) {
			reverse = reverseOrder;
		}

		@Override
		public int compare(final Object objectA, final Object objectB) {
			int priorityA = getPriorityIndex(objectA);
			int priorityB = getPriorityIndex(objectB);
			int result = 0;
			if (priorityA != priorityB) {
				return priorityA - priorityB;
			}

			// These objects are of the same type.
			if (objectA.equals(objectB)) {
				result = 0;
			}

			if (objectA instanceof Collection) {
				result = ((Collection<?>) objectA).size() - ((Collection<?>) objectB).size();
			}

			if (objectA instanceof Number) {
				Number numberA = (Number) objectA;
				Number numberB = (Number) objectB;

				result = Double.compare(numberA.doubleValue(), numberB.doubleValue());
			}
			if (objectA instanceof Boolean) {
				boolean booleanA = (boolean) objectA;
				boolean booleanB = (boolean) objectB;
				result = Boolean.compare(booleanA, booleanB);
			}
			if (objectA instanceof Entry) {
				result = ((Entry<String, Object>) objectA).getValue().toString().compareTo(((Entry<String, Object>) objectB).getValue().toString());
			}
			if(objectA instanceof String){
				result = objectA.toString().compareTo(objectB.toString());
			}

			if (reverse) {
				result = -result;
			}

			return result;
		}

	}
}
