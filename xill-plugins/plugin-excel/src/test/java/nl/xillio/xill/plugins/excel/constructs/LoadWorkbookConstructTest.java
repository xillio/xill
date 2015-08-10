package nl.xillio.xill.plugins.excel.constructs;

import nl.xillio.xill.api.Xill;
import nl.xillio.xill.api.components.MetaExpression;
import nl.xillio.xill.api.components.RobotID;
import nl.xillio.xill.api.construct.ConstructContext;
import nl.xillio.xill.plugins.excel.dataStructures.XillWorkbook;
import nl.xillio.xill.plugins.excel.services.ExcelService;
import nl.xillio.xill.plugins.excel.services.ExcelServiceImpl;
import nl.xillio.xill.services.inject.InjectorUtils;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.testng.annotations.Test;

import java.io.File;
import java.lang.reflect.Field;

import static org.mockito.Mockito.*;

import static org.testng.Assert.*;

/**
 * Created by Daan Knoope on 10-8-2015.
 */
public class LoadWorkbookConstructTest {

	@Test
	public void testProcessNormal() throws Exception {

		InjectorUtils.getGlobalInjector(); //TODO util package
		ExcelService service = mock(ExcelService.class);
		XillWorkbook workbook = mock(XillWorkbook.class);
		ConstructContext context = mock(ConstructContext.class);
		RobotID id = mock(RobotID.class);
		when(id.getPath()).thenReturn(new File("."));
		when(context.getRobotID()).thenReturn(id);

		when(service.loadWorkbook(same(context), anyString(), any(File.class))).thenReturn(workbook);

		MetaExpression filePath = mock(MetaExpression.class);
		when(filePath.getStringValue()).thenReturn("testpath");
		MetaExpression expression = LoadWorkbookConstruct.process(service, context, filePath);


		// Verify
		verify(service, times(1)).loadWorkbook(any(), anyString(), any());

		// Assert
		assertEquals(expression.getMeta(XillWorkbook.class), workbook);

		Row row = mock(Row.class);
		Sheet sheet = mock(Sheet.class);
		when(sheet.getLastRowNum()).thenReturn(2);
		when(sheet.getRow(anyInt())).thenReturn(row);
		when(row.getLastCellNum()).thenReturn((short)5, (short)3, (short)6);


		assertEquals(new ExcelServiceImpl().columnSize(sheet), 7);


	}
}

