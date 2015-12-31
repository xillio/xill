package nl.xillio.xill.plugins.collection.services.duplicate;

import com.google.inject.Singleton;

import java.util.*;
import java.util.Map.Entry;

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
    private Object deepCopyList(Object input, IdentityHashMap<Object, Object> results) {
        List<Object> copy = new ArrayList<Object>();
        if (results.containsKey(input)) {
            return results.get(input);
        }

        if (input instanceof List) {
            List<Object> list = (List<Object>) input;
            List<Object> dup = copy(list);
            results.put(list, dup);

            for (int i = 0; i < dup.size(); i++) {
                Object child = dup.get(i);
                dup.set(i, deepCopyList(child, results));
            }

            return dup;
        } else if (input instanceof Map) {
            Entry<String, Object>[] sortedEntries = ((Map<String, Object>) input)
                    .entrySet()
                    .stream()
                    .toArray(Entry[]::new);

            Map<String, Object> map = new LinkedHashMap<>();
            for (Entry<String, Object> entry : sortedEntries) {
                map.put(entry.getKey(), entry.getValue());
            }

            for (String key : map.keySet()) {
                Object child = map.get(key);
                map.put(key, deepCopyList(child, results));
            }
            return map;
        }
        return input;
    }

    private List<Object> copy(List<Object> input) {
        List<Object> output = new ArrayList<>();
        for (Object o : input) {
            output.add(o);
        }
        return output;
    }
}
