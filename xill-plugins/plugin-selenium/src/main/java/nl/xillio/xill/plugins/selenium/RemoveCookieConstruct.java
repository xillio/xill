package nl.xillio.xill.plugins.selenium;

import java.util.List;
import org.openqa.selenium.WebDriver;
import nl.xillio.xill.api.components.ExpressionBuilder;
import nl.xillio.xill.api.components.ExpressionDataType;
import nl.xillio.xill.api.components.MetaExpression;
import nl.xillio.xill.api.construct.Argument;
import nl.xillio.xill.api.construct.Construct;
import nl.xillio.xill.api.construct.ConstructContext;
import nl.xillio.xill.api.construct.ConstructProcessor;
import nl.xillio.xill.api.errors.RobotRuntimeException;

public class RemoveCookieConstruct implements Construct {

	@Override
	public String getName() {
		return "removecookie";
	}

	@Override
	public ConstructProcessor prepareProcess(ConstructContext context) {
		return new ConstructProcessor(
			RemoveCookieConstruct::process,
			new Argument("page"),
			new Argument("cookie"));
	}

	public static MetaExpression process(final MetaExpression pageVar, final MetaExpression cookieVar) {
		
		if (cookieVar.isNull()) {
			return ExpressionBuilder.NULL;
		}
		//else
		
		if (!PageVariable.checkType(pageVar)) {
			throw new RobotRuntimeException("Invalid variable type. PAGE type expected!");
		}
		//else

		WebDriver driver = PageVariable.getDriver(pageVar);
				
		try {
			
			if (cookieVar.getType() == ExpressionDataType.LIST) {
				@SuppressWarnings("unchecked")
				List<MetaExpression> list = (List<MetaExpression>) cookieVar.getValue();
				for (MetaExpression cookie : list) {
					driver.manage().deleteCookieNamed(cookie.getStringValue());
				}
			} else {
				Object value = MetaExpression.extractValue(cookieVar);
				if (value instanceof Integer) {//boolean type cannot be determined in Xill 3.0 (at least for now)
					if (cookieVar.getBooleanValue()) {
						driver.manage().deleteAllCookies();
					}
				} else if (value instanceof String) {
					driver.manage().deleteCookieNamed(value.toString());
				} else {
					throw new RobotRuntimeException("Invalid cookie type!");
				}
			}
		} catch (Exception e) {
			throw new RobotRuntimeException(e.getClass().getSimpleName(), e);
		}

		return ExpressionBuilder.NULL;
	}
}