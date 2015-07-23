package nl.xillio.xill.plugins.list.util;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Stack;
import java.util.Map.Entry;

import nl.xillio.xill.api.components.ExpressionBuilder;
import nl.xillio.xill.api.components.MetaExpression;

/**
 * @author Sander
 *
 */
public class Reverse {


	List<Object> disc = new ArrayList<>();
	Map<String,Object> disc2 = new LinkedHashMap<String, Object>();

	/**
	 * @param input the list
	 * @param recursive whether lists inside the list should be reversed too.
	 * @return the reversed list.
	 */
	@SuppressWarnings("unchecked")
	public Object reverted(final Object input,final boolean recursive) {
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
					if(recursive){
					if (m instanceof List<?>) {
						reverted(m,recursive);
					}
					if(m instanceof Map<?,?>){
						reverted(m,recursive);
					}}
				
				}
				((List<Object>) input).clear();
				int size = s.size();
				for (int i = 0; i < size; i++) {
					((List<Object>) input).add(s.pop());
				}

			
		}else if(input instanceof Map<?,?>){
			Map<String,Object> list = (Map<String,Object>)input;
			Stack<Entry<String,Object>> s = new Stack<>();
			disc2.putAll(list);
			outerloop: for (Entry<String, Object> entry : list.entrySet()) {
				s.push(entry);

				for (Entry<String, Object> entry2 : disc2.entrySet()) {
					{
						if (entry2.equals(entry)) {
							continue outerloop;
						}
					}}
				if(recursive){
					if (entry.getValue() instanceof List<?>) {
						entry.setValue(reverted(entry.getValue(),recursive));
					}
					if(entry.getValue() instanceof Map<?,?>){
						entry.setValue(reverted(entry.getValue(),recursive));
					}}
				
				}
				((Map<String,Object>) input).clear();
				int size = s.size();
				for (int i = 0; i < size; i++) {
					Entry<String,Object> e = s.pop();
					((Map<String,Object>) input).put(e.getKey().toString(), e.getValue());
				}
				
		}
		return input;
	}
}
