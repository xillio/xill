package nl.xillio.xill.plugins.list.services.duplicate;

import java.util.ArrayList;
import java.util.IdentityHashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.stream.Collectors;

import com.google.inject.Singleton;

/**
 * This is the main implementation of {@link Duplicate}.
 *
 * @author Sander Visser
 */
@Singleton
public class DuplicateImpl implements Duplicate {

	@SuppressWarnings("unchecked")
	@Override
	public Object duplicate(final Object input) {
		Object copy = new Object();
		if (input instanceof List<?>) {
			copy = deepCopyList(input, new IdentityHashMap<>()); // input is a list.
		} else {
			copy = deepCopyList(input, new IdentityHashMap<>());
			//copy = deepCopyMap(input,new IdentityHashMap<>()); // input is an object
		}
		return copy;
	}

	@SuppressWarnings("unchecked")
	private Object deepCopyList(Object input,IdentityHashMap<Object, Object> results) {
		List<Object> copy = new ArrayList<Object>();
		if (results.containsKey(input)) {
			return results.get(input);
		}
		
		if(input instanceof List){
			List<Object> list = (List<Object>) input;
			List<Object> dup = copy(list);
			results.put(list, dup);
			
			for (int i = 0; i < dup.size(); i++) {
				Object child = dup.get(i);
				dup.set(i, deepCopyList(child,results));
			}
		
		return dup;
		} else if (input instanceof Map) {
			Map.Entry<String, Object>[] sortedEntries = ((Map<String, Object>) input)
					.entrySet()
					.stream()
					.toArray(Entry[]::new);

			Map<String, Object> map = new LinkedHashMap<>();
			for (Entry<String, Object> entry : sortedEntries) {
				map.put(entry.getKey(), entry.getValue());
			}

				for (String key : map.keySet()) {
					Object child = map.get(key);
					map.put(key, deepCopyList(child,results));
				}
			return map;
		}
		return input;
	}
	
	
	
	
		/*
		for (Object m : input) {
			if (m instanceof List<?>) {
				List<?> list = (List<?>) m;
				copy.add(deepCopyList((List<Object>) m,results)); // element in list is a list so copy that list too
				results.put(list, copy);
			} else if (m instanceof LinkedHashMap<?, ?>) {
				copy.add(deepCopyMap((LinkedHashMap<String, Object>) m,results)); // element in list is object, copy this object too.
			} else {
				copy.add(m);
			}
		}
		return copy;
	}

	@SuppressWarnings("unchecked")
	private Object deepCopyMap(LinkedHashMap<String, Object> input,IdentityHashMap<Object, Object> results) {
		LinkedHashMap<String, Object> copy = new LinkedHashMap<>();
		for (Entry<String, Object> entry : input.entrySet()) {
			if (entry.getValue() instanceof List<?>) {
				copy.put(entry.getKey(), deepCopyList((List<Object>) entry.getValue(),results)); // value is a list.
			} else if (entry.getValue() instanceof LinkedHashMap<?, ?>) {
				copy.put(entry.getKey(), deepCopyMap((LinkedHashMap<String, Object>) entry.getValue(),results)); // value is an object.
			} else {
				copy.put(entry.getKey(), entry.getValue());
			}
		}
		return copy;
	}
*/
	private List<Object> copy(List<Object> input){
		List<Object> output = new ArrayList<>();
		for(Object o : input){
			output.add(o);
		}
		return output;
	}
}
