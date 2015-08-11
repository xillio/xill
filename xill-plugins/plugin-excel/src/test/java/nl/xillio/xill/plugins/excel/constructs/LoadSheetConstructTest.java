package nl.xillio.xill.plugins.excel.constructs;

import nl.xillio.xill.api.components.MetaExpression;
import nl.xillio.xill.api.construct.ConstructContext;
import nl.xillio.xill.api.errors.RobotRuntimeException;
import nl.xillio.xill.plugins.excel.dataStructures.XillWorkbook;
import nl.xillio.xill.plugins.excel.services.ExcelService;
import nl.xillio.xill.services.inject.InjectorUtils;
import org.testng.annotations.Test;

import static nl.xillio.xill.api.construct.ExpressionBuilderHelper.fromValue;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.mock;
import static org.testng.Assert.*;

/**
 * Created by Daan Knoope on 11-8-2015.
 */
public class LoadSheetConstructTest {

	@Test(expectedExceptions = RobotRuntimeException.class, expectedExceptionsMessageRegExp = "Expected parameter 'workbook' to be a result of loadWorkbook or createWorkbook")
	public void testProcessNoWorkbook() throws Exception {
		InjectorUtils.getGlobalInjector();
		XillWorkbook workbook = null;//mock(XillWorkbook.class);
		MetaExpression input = fromValue("workbook object");
		input.storeMeta(XillWorkbook.class, workbook);
		ExcelService service = mock(ExcelService.class);
		LoadSheetConstruct.process(service, input, fromValue("Sheet"));
	}
}
