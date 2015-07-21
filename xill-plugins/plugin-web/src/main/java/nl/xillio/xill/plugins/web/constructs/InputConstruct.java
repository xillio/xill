package nl.xillio.xill.plugins.web.constructs;

import org.openqa.selenium.WebElement;

import nl.xillio.xill.api.components.MetaExpression;
import nl.xillio.xill.api.construct.Argument;
import nl.xillio.xill.api.construct.Construct;
import nl.xillio.xill.api.construct.ConstructContext;
import nl.xillio.xill.api.construct.ConstructProcessor;
import nl.xillio.xill.api.errors.RobotRuntimeException;
import nl.xillio.xill.plugins.web.NodeVariable;

public class InputConstruct extends Construct {

	@Override
	public ConstructProcessor prepareProcess(final ConstructContext context) {
		return new ConstructProcessor(InputConstruct::process, new Argument("element"), new Argument("text"));
	}

	public static MetaExpression process(final MetaExpression elementVar, final MetaExpression textVar) {

		if (!NodeVariable.checkType(elementVar)) {
			throw new RobotRuntimeException("Invalid variable type. NODE type expected!");
		}
		// else

		String text = textVar.getStringValue();

		WebElement element = NodeVariable.get(elementVar);

		try {
			element.clear();
			element.sendKeys(text);
		} catch (Exception e) {
			throw new RobotRuntimeException(e.getClass().getSimpleName(), e);
		}

		return NULL;
	}

}
