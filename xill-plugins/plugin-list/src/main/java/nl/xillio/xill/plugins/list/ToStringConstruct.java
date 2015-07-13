package nl.xillio.xill.plugins.list;

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
public class ToStringConstruct implements Construct {

	@Override
	public String getName() {

		return "toString";
	}

	@Override
	public ConstructProcessor prepareProcess(final ConstructContext context) {
		return new ConstructProcessor(ToStringConstruct::process, new Argument("list"));
	}

	private static MetaExpression process(final MetaExpression listVar) {

		if(listVar.getType() != ExpressionDataType.ATOMIC){
			return ExpressionBuilder.fromValue(listVar.getStringValue());
		}
		return listVar;
		
	}
}
