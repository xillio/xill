package nl.xillio.xill.plugins.excel.constructs;

import nl.xillio.xill.api.components.MetaExpression;
import nl.xillio.xill.api.components.RobotID;
import nl.xillio.xill.api.construct.ConstructContext;
import nl.xillio.xill.api.errors.RobotRuntimeException;
import nl.xillio.xill.plugins.excel.datastructures.XillWorkbook;
import nl.xillio.xill.plugins.excel.services.ExcelService;
import nl.xillio.xill.services.inject.InjectorUtils;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import java.io.File;
import java.io.IOException;

import static nl.xillio.xill.api.construct.ExpressionBuilderHelper.NULL;
import static nl.xillio.xill.api.construct.ExpressionBuilderHelper.fromValue;
import static org.mockito.Mockito.*;
import static org.testng.Assert.assertEquals;

/**
 * Created by Daan Knoope on 17-8-2015.
 */
public class SaveConstructTest {

	@BeforeClass
	public void initializeInjector() {
		InjectorUtils.getGlobalInjector();
	}

	@Test(expectedExceptions = RobotRuntimeException.class,
					expectedExceptionsMessageRegExp = "Expected parameter 'workbook' to be a result of loadWorkbook or createWorkbook")
	public void testProcessNoValidWorkbook() throws Exception {
		ExcelService service = mock(ExcelService.class);
		ConstructContext context = mock(ConstructContext.class);
		SaveConstruct.process(service, context, fromValue((String) null), fromValue("path"));
	}

	@Test
	public void testProcessOverrideByDefault() throws Exception {
		ExcelService service = mock(ExcelService.class);
		ConstructContext context = mock(ConstructContext.class);
		XillWorkbook workbook = mock(XillWorkbook.class);
		MetaExpression workbookInput = fromValue("workbook");
		workbookInput.storeMeta(XillWorkbook.class, workbook);
		when(service.save(any(XillWorkbook.class))).thenReturn("overridden");
		assertEquals(SaveConstruct.process(service, context, workbookInput, NULL).getStringValue(), "overridden");
	}

	@Test
	public void testProcessPath() throws Exception {
		ExcelService service = mock(ExcelService.class);
		ConstructContext context = mock(ConstructContext.class);
		RobotID robotID = mock(RobotID.class);
		when(context.getRobotID()).thenReturn(robotID);
		when(robotID.getPath()).thenReturn(new File("."));
		XillWorkbook workbook = mock(XillWorkbook.class);
		MetaExpression workbookInput = fromValue("workbook");
		workbookInput.storeMeta(XillWorkbook.class, workbook);
		MetaExpression inputPath = fromValue("path");
		when(service.save(any(File.class), any(XillWorkbook.class))).thenReturn("saved to dir");
		assertEquals(SaveConstruct.process(service, context, workbookInput, inputPath).getStringValue(), "saved to dir");
	}

	@Test(expectedExceptions = RobotRuntimeException.class)
	public void testProcessOverwriteThrowsException() throws Exception {
		ExcelService service = mock(ExcelService.class);
		XillWorkbook workbook = mock(XillWorkbook.class);
		doThrow(new IOException()).when(service).save(any(XillWorkbook.class));
		SaveConstruct.processOverwrite(service, workbook);
	}

	@Test
	public void testProcessOverwrite() throws Exception {
		ExcelService service = mock(ExcelService.class);
		XillWorkbook workbook = mock(XillWorkbook.class);
		when(service.save(any(XillWorkbook.class))).thenReturn("Correctly Saved");
		assertEquals(SaveConstruct.processOverwrite(service, workbook), fromValue("Correctly Saved"));
	}

	@Test(expectedExceptions = RobotRuntimeException.class)
	public void testProcessToFolderThrowsException() throws Exception {
		ExcelService service = mock(ExcelService.class);
		XillWorkbook workbook = mock(XillWorkbook.class);
		when(service.save(any(File.class), any(XillWorkbook.class))).thenThrow(new IOException());
		SaveConstruct.processToFolder(service, workbook, mock(File.class));
	}

	@Test
	public void testProcessToFolder() throws Exception {
		ExcelService service = mock(ExcelService.class);
		XillWorkbook workbook = mock(XillWorkbook.class);
		when(service.save(any(File.class), any(XillWorkbook.class))).thenReturn("correct");
		assertEquals(fromValue("correct"), SaveConstruct.processToFolder(service, workbook, mock(File.class)));
	}
}
