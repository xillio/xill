package nl.xillio.xill.plugins.selenium.constructs;

import org.openqa.selenium.WebElement;

import nl.xillio.xill.api.components.MetaExpression;
import nl.xillio.xill.api.construct.Argument;
import nl.xillio.xill.api.construct.Construct;
import nl.xillio.xill.api.construct.ConstructContext;
import nl.xillio.xill.api.construct.ConstructProcessor;
import nl.xillio.xill.api.errors.RobotRuntimeException;
import nl.xillio.xill.plugins.selenium.NodeVariable;

public class SelectedConstruct extends Construct {

	@Override
	public String getName() {
		return "selected";
	}

	@Override
	public ConstructProcessor prepareProcess(ConstructContext context) {
		return new ConstructProcessor(
			SelectedConstruct::process,
			new Argument("element"));
	}

	public static MetaExpression process(final MetaExpression elementVar) {
		
		if (!NodeVariable.checkType(elementVar)) {
			throw new RobotRuntimeException("Invalid variable type. NODE type expected!");
		}
		//else
		
		WebElement element = NodeVariable.get(elementVar);
				
		try {
			return fromValue(element.isSelected());
		} catch (Exception e) {
			throw new RobotRuntimeException(e.getClass().getSimpleName(), e);
		}
	}
}