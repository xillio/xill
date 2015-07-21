package nl.xillio.xill.plugins.list.util;

import java.util.ArrayList;
import java.util.List;
import java.util.Stack;

import nl.xillio.xill.api.components.ExpressionBuilder;
import nl.xillio.xill.api.components.ExpressionDataType;
import nl.xillio.xill.api.components.MetaExpression;

public class Reverse {

	/**
	 * 
	 */
	List<List<MetaExpression>> disc = new ArrayList<>();

	@SuppressWarnings("unchecked")
	public void reverted(final List<MetaExpression> input) {

	Stack<MetaExpression> s = new Stack<>();
	disc.add(input);
	outerloop: for (MetaExpression m : input) {
		s.push(m);

		for (List<MetaExpression> e : disc) {
			for(MetaExpression r : e){
		if (r == m) {
			continue outerloop;}
		}
		}

		if (m.getType() == ExpressionDataType.LIST) {
		reverted((List<MetaExpression>)m.getValue());
		}

	}
	input.clear();
	int size = s.size();
	for(int i = 0; i < size; i++){
		input.add(s.pop());
	}
	
	}
}
