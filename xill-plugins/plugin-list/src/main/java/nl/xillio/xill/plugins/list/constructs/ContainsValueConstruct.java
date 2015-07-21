package nl.xillio.xill.plugins.list.constructs;

import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;

import nl.xillio.xill.api.components.ExpressionBuilder;
import nl.xillio.xill.api.components.ExpressionDataType;
import nl.xillio.xill.api.components.MetaExpression;
import nl.xillio.xill.api.construct.Argument;
import nl.xillio.xill.api.construct.Construct;
import nl.xillio.xill.api.construct.ConstructContext;
import nl.xillio.xill.api.construct.ConstructProcessor;
import nl.xillio.xill.api.errors.RobotRuntimeException;

/**
 *
 *
 * 
 *
 * @author Sander
 *
 */
public class ContainsValueConstruct extends Construct {

	@Override
	public String getName() {

		return "containsValue";
	}

	@Override
	public ConstructProcessor prepareProcess(final ConstructContext context) {
		return new ConstructProcessor(ContainsValueConstruct::process, new Argument("list"),new Argument("value"));
	}

	private static MetaExpression process(final MetaExpression listVar,final MetaExpression valueVar) {

		if(listVar.getType() ==  LIST){
			List<MetaExpression> list = (List<MetaExpression>)listVar.getValue();
				return fromValue(list.contains(valueVar));
			}else if(listVar.getType() == OBJECT){
				LinkedHashMap<String, MetaExpression> object = (LinkedHashMap<String,MetaExpression>)listVar.getValue();
			  return fromValue(object.containsValue(valueVar));
			}
	return NULL;
	}
}
