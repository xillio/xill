package nl.xillio.xill.plugins.list.services.reverse;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Stack;

import com.google.inject.Singleton;

/**
 *
 * This is the main implementation of {@link Reverse}.
 *
 * @author Sander Visser
 *
 */
@Singleton
public class ReverseImpl implements Reverse {

	@Override
	public Object asReversed(final Object input, final boolean recursive) {
		if (input instanceof List<?>) {
			return asReversedIteration(input, recursive, new ArrayList<Object>());
		}
		else {
			return asReversedIteration(input, recursive, new LinkedHashMap<String, Object>());
		}
	}

	@SuppressWarnings("unchecked")
	private Object asReversedIteration(final Object input, final boolean recursive, final List<Object> disc) {
		if (input instanceof List<?>) {
			List<Object> list = (List<Object>) input;
			Stack<Object> s = new Stack<>();
			disc.add(input);
			outerloop: for (Object m : list) {
				s.push(m);

				for (Object e : disc) {
					{
						if (e == m) {
							continue outerloop;
						}
					}
				}
				if (recursive) {
					if (m instanceof List<?>) {
						asReversedIteration(m, recursive, disc);
					}
					if (m instanceof Map<?, ?>) {
						asReversedIteration(m, recursive, disc);
					}
				}

			}
			((List<Object>) input).clear();
			int size = s.size();
			for (int i = 0; i < size; i++) {
				((List<Object>) input).add(s.pop());
			}

		}
		return input;
	}

	@SuppressWarnings("unchecked")
	private Object asReversedIteration(final Object input, final boolean recursive, final Map<String, Object> disc) {
		Map<String, Object> list = (Map<String, Object>) input;
		Stack<Entry<String, Object>> s = new Stack<>();
		disc.putAll(list);
		outerloop: for (Entry<String, Object> entry : list.entrySet()) {
			s.push(entry);

			for (Entry<String, Object> entry2 : disc.entrySet()) {
				{
					if (entry2.equals(entry)) {
						continue outerloop;
					}
				}
			}
			if (recursive) {
				if (entry.getValue() instanceof List<?>) {
					entry.setValue(asReversedIteration(entry.getValue(), recursive, disc));
				}
				if (entry.getValue() instanceof Map<?, ?>) {
					entry.setValue(asReversedIteration(entry.getValue(), recursive, disc));
				}
			}

		}
		((Map<String, Object>) input).clear();
		int size = s.size();
		for (int i = 0; i < size; i++) {
			Entry<String, Object> e = s.pop();
			((Map<String, Object>) input).put(e.getKey().toString(), e.getValue());
		}
		return input;
	}
}
