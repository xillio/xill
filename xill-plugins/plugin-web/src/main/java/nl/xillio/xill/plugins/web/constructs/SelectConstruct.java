package nl.xillio.xill.plugins.web.constructs;

import org.openqa.selenium.WebElement;

import nl.xillio.xill.api.components.MetaExpression;
import nl.xillio.xill.api.construct.Argument;
import nl.xillio.xill.api.construct.Construct;
import nl.xillio.xill.api.construct.ConstructContext;
import nl.xillio.xill.api.construct.ConstructProcessor;
import nl.xillio.xill.api.errors.RobotRuntimeException;
import nl.xillio.xill.plugins.web.NodeVariable;

public class SelectConstruct extends Construct {

	@Override
	public ConstructProcessor prepareProcess(final ConstructContext context) {
		return new ConstructProcessor(
			SelectConstruct::process,
			new Argument("element"),
			new Argument("select"));
	}

	public static MetaExpression process(final MetaExpression elementVar, final MetaExpression selectVar) {

		if (!NodeVariable.checkType(elementVar)) {
			throw new RobotRuntimeException("Invalid variable type. NODE type expected!");
		}
		// else

		boolean select = selectVar.getBooleanValue();

		WebElement element = NodeVariable.get(elementVar);

		try {
			if (select && !element.isSelected() || !select && element.isSelected()) { // if it's <option> tag then "deselect" doesn't work
				element.click();
			}
			return NULL;
		} catch (Exception e) {
			throw new RobotRuntimeException(e.getClass().getSimpleName(), e);
		}
	}
}
