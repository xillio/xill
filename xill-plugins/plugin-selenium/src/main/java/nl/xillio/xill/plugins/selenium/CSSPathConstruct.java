package nl.xillio.xill.plugins.selenium;

import java.util.ArrayList;
import java.util.List;

import org.openqa.selenium.By;
import org.openqa.selenium.InvalidElementStateException;
import org.openqa.selenium.InvalidSelectorException;
import org.openqa.selenium.SearchContext;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

import nl.xillio.xill.api.components.ExpressionBuilder;
import nl.xillio.xill.api.components.MetaExpression;
import nl.xillio.xill.api.construct.Argument;
import nl.xillio.xill.api.construct.Construct;
import nl.xillio.xill.api.construct.ConstructContext;
import nl.xillio.xill.api.construct.ConstructProcessor;
import nl.xillio.xill.api.errors.RobotRuntimeException;

public class CSSPathConstruct implements Construct {

	@Override
	public String getName() {
		return "csspath";
	}

	@Override
	public ConstructProcessor prepareProcess(ConstructContext context) {
		return new ConstructProcessor(
			CSSPathConstruct::process,
			new Argument("element"),
			new Argument("csspath"));
	}

	public static MetaExpression process(final MetaExpression elementVar, final MetaExpression cssPathVar) {
		
		String query = cssPathVar.getStringValue();

		if (elementVar.isNull()) {
			return ExpressionBuilder.NULL;
		} else if (NodeVariable.checkType(elementVar)) {
			return processSELNode(NodeVariable.getDriver(elementVar), NodeVariable.get(elementVar), query);
		} else if (PageVariable.checkType(elementVar)) {
			return processSELNode(PageVariable.getDriver(elementVar), PageVariable.getDriver(elementVar), query);
		} else {
			throw new RobotRuntimeException("Invalid variable type. PAGE or NODE type expected!");
		}
	}		
	
	private static MetaExpression processSELNode(WebDriver driver, SearchContext node, String selector) {

		try {
			List<WebElement> results = node.findElements(By.cssSelector(selector));

			if (results.size() == 0) {
				// log.debug("No results");
				return ExpressionBuilder.NULL;
			} else if (results.size() == 1) {
				return NodeVariable.create(driver, results.get(0));
			} else {
				ArrayList<MetaExpression> list = new ArrayList<MetaExpression>();

				for (WebElement he : results) {
					list.add(NodeVariable.create(driver, he));
				}
				
				return ExpressionBuilder.fromValue(list);
			}
		} catch (InvalidElementStateException e) {
			throw new RobotRuntimeException("Invalid CSSPath", e);
		} catch (InvalidSelectorException e) {
			throw new RobotRuntimeException("Invalid CSSPath", e);
		}
	}

}