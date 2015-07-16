package nl.xillio.xill.plugins.list;

import java.util.ArrayList;
import java.util.Collections;
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
public class ReverseConstruct implements Construct {

	@Override
	public String getName() {

		return "reverse";
	}

	@Override
	public ConstructProcessor prepareProcess(final ConstructContext context) {
		return new ConstructProcessor(ReverseConstruct::process, new Argument("list"),new Argument("recursive"));
	}

	private static MetaExpression process(final MetaExpression listVar, final MetaExpression recursiveVar) {

		boolean reverseRecursive = recursiveVar.getBooleanValue();

		if (listVar == ExpressionBuilder.NULL) {
			return ExpressionBuilder.NULL;
		}

		List<MetaExpression> list = (List<MetaExpression>)listVar.getValue();
		List<MetaExpression> reversedList = new ArrayList<>(list);

		if (reverseRecursive) {
			reversedList = processList(list);
		} else {
			Collections.reverse(reversedList);
		}

		return ExpressionBuilder.fromValue(reversedList);
	}
		
	public static List<MetaExpression> processList(List<MetaExpression> listVar){
		List<MetaExpression> reversedList = new ArrayList<>();
		for(MetaExpression e : listVar){
			if(e.getType() == ExpressionDataType.LIST){
				
				List<MetaExpression> subList = (List<MetaExpression>)e.getValue();
				
				reversedList.add(ExpressionBuilder.fromValue(subList));
				
			}
		}
		return listVar;
	}
	}

