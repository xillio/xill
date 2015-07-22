package nl.xillio.xill.plugins.list.util;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Stack;
import java.util.Map.Entry;

import nl.xillio.xill.api.components.ExpressionBuilder;
import nl.xillio.xill.api.components.MetaExpression;

public class Sort {

	/**
	 *
	 */

	List<Object> disc = new ArrayList<>();

	@SuppressWarnings("unchecked")
	public Object doSorting(final Object input,final boolean recursive) {
		List<String> stringElement = new ArrayList<>();
		List<Object> numberElement = new ArrayList<>();
		List<Object> listElement = new ArrayList<>();
		Map<String,Object> mapElement = new LinkedHashMap<String, Object>();
		disc.add(input);
		for(Object o : (List<Object>)input){
		if (o instanceof List<?>) {
		 	if(recursive && !checkCircular(o)){
					listElement.add(doSorting(o,recursive));
				}else{
					listElement.add(o);
				}
			}
		 else if(o instanceof Map<?,?>){
			Map<String,Object> list = (Map<String, Object>)o;
			mapElement.putAll(list);
	}
	else {
		if(o instanceof Integer){
			numberElement.add(((Number)o).doubleValue());
		}else if(o instanceof String){
			stringElement.add(o.toString());
		}
		
	}
		}
		
		clearInput(input);
		
		if(!listElement.isEmpty()){
		((List<Object>) input).addAll(listElement);
		}
		if(!numberElement.isEmpty()){
			((List<Object>)input).addAll(sortThings(numberElement));
		}
		if(!stringElement.isEmpty()){
		//	((List<Object>)input).addAll(Collections.sort((List<Object>)stringElement, String.CASE_INSENSITIVE_ORDER));
		}
		
		
		return input;
		
}
	public boolean checkCircular(final Object input){
		for (Object e : disc) {
			
				if (e == input) {
					return true; //there is a CR.
				}
		}return false;
	}
	
	public void clearInput(Object input){
		if(input instanceof List<?>){
			((List<Object>) input).clear();
		}else if(input instanceof Map<?,?>){
			((Map<String,Object>) input).clear();
		}else{
			input = null;
		}}
		
	public List<Object> sortThings(List<Object> list){
		Collections.sort(list, (a1, a2) -> {
			double v1 = (double)a1;
			double v2 = (double)a2;
			return Double.compare(v1, v2);
		});
	return list;
	}
}
