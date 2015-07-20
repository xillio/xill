package nl.xillio.xill.plugins.list;

import java.util.Iterator;
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
public class ToCSVConstruct extends Construct {

	@Override
	public String getName() {

		return "toCSV";
	}

	@Override
	public ConstructProcessor prepareProcess(final ConstructContext context) {
		return new ConstructProcessor(ToCSVConstruct::process, new Argument("list"),new Argument("delimiter"),new Argument("enclosingChar"));
	}

	private static MetaExpression process(final MetaExpression listVar,final MetaExpression delimVar, final MetaExpression encloseVar) {

		if(listVar.getType() == ExpressionDataType.LIST){
			@SuppressWarnings("unchecked")
			List<MetaExpression> listValue = (List<MetaExpression>)listVar.getValue();
			String result = "";	
			String delimiter = delimVar.getStringValue();
			String enclosing = encloseVar.getStringValue();
			
			for (Iterator<MetaExpression> it = listValue.iterator(); it.hasNext(); ) {
			    
				result += delimiter+enclosing+it.next().getStringValue()+enclosing;
			    }
			if(result != ""){
				result = result.substring(delimiter.length());
			}
			
			return ExpressionBuilder.fromValue(result);
		}
		return listVar;
		
	}
}
