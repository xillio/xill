package nl.xillio.xill.plugins.list.constructs;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import com.google.inject.Inject;

import nl.xillio.xill.api.components.MetaExpression;
import nl.xillio.xill.api.construct.Argument;
import nl.xillio.xill.api.construct.Construct;
import nl.xillio.xill.api.construct.ConstructContext;
import nl.xillio.xill.api.construct.ConstructProcessor;
import nl.xillio.xill.api.errors.NotImplementedException;


/**
 * Returns true if the value is contained in the given list or object otherwise false.
 *
 * @author Sander Visser
 *
 */
public class ContainsValueConstruct extends Construct {


	@Override
	public ConstructProcessor prepareProcess(final ConstructContext context) {
		return new ConstructProcessor((input, value) -> process(input, value), new Argument("input", LIST, OBJECT), new Argument("value",ATOMIC));
	}

   /**
   * @param input the list or object.
   * @param value the value that is going to be checked.
   * @return true if the list or object contains the value.
   */
  static MetaExpression process(final MetaExpression input, final MetaExpression value) {
  	 boolean result = false;
  	switch(input.getType()) {
			case OBJECT:
			  @SuppressWarnings("unchecked")
				Map<String,MetaExpression> m = (Map<String,MetaExpression>)input.getValue();
			  result = m.containsValue(value);
				break;
			case LIST:
				@SuppressWarnings("unchecked")
				List<MetaExpression> l = (List<MetaExpression>)input.getValue();
				result = l.contains(value);
				break;
			default:
				throw new NotImplementedException("This type is not implemented.");
  		
  	}
	
		return fromValue(result);
	}
}
