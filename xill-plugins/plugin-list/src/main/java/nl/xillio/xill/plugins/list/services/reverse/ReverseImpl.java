package nl.xillio.xill.plugins.list.services.reverse;

import com.google.inject.Singleton;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Stack;

/**
 * This is the main implementation of {@link Reverse}.
 *
 * @author Sander Visser
 */
@Singleton
@SuppressWarnings("unchecked")
public class ReverseImpl implements Reverse {

    //checks whether it is a list or an Object
    @Override
    public Object asReversed(final Object input, final boolean recursive) {
        if (input instanceof List<?>) {
            return asReversedIteration((List<Object>) input, recursive, new ArrayList<>());
        } else {
            return asReversedIteration((Map<String, Object>) input, recursive, new ArrayList<>());
        }
    }

    //reverses the given input. if recursive is true is reverses list and objects inside the given list.
    private Object asReversedIteration(final List<Object> list, final boolean recursive, final List<Object> disc) {
        Stack<Object> s = new Stack<>();
        disc.add(list);
        outerLoop:
        for (Object m : list) {
            s.push(m);

            for (Object e : disc) {
                {
                    if (e == m) {
                        continue outerLoop;
                    }
                }
            }
            if (recursive) {
                if (m instanceof List<?>) {
                    asReversedIteration((List<Object>) m, true, disc);
                }
                if (m instanceof Map<?, ?>) {
                    asReversedIteration((Map<String, Object>) m, true, disc);
                }
            }

        }
        list.clear();
        int size = s.size();
        for (int i = 0; i < size; i++) {
            list.add(s.pop());
        }
        return list;
    }

    //reverses the given input. if recursive is true is reverses list and objects inside the given object
    @SuppressWarnings("unchecked")
    private Object asReversedIteration(final Map<String, Object> input, final boolean recursive, final List<Object> disc) {
        Stack<Entry<String, Object>> s = new Stack<>();
        disc.add(input);
        outerLoop:
        for (Entry<String, Object> entry : input.entrySet()) {
            s.push(entry);

            for (Object e : disc) {
                {
                    if (e == entry) {
                        continue outerLoop;
                    }
                }
            }
            if (recursive) {
                if (entry.getValue() instanceof List<?>) {
                    entry.setValue(asReversedIteration((List<Object>) entry.getValue(), true, disc));
                }
                if (entry.getValue() instanceof Map<?, ?>) {
                    entry.setValue(asReversedIteration((Map<String, Object>) entry.getValue(), true, disc));
                }
            }

        }
        input.clear();
        int size = s.size();
        for (int i = 0; i < size; i++) {
            Entry<String, Object> e = s.pop();
            input.put(e.getKey(), e.getValue());
        }
        return input;
    }
}
