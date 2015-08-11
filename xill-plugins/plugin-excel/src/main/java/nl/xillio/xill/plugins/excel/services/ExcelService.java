package nl.xillio.xill.plugins.excel.services;

import com.google.inject.ImplementedBy;
import nl.xillio.xill.api.construct.ConstructContext;
import nl.xillio.xill.plugins.excel.dataStructures.XillWorkbook;
import org.apache.log4j.spi.RootLogger;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.text.ParseException;

/**
 * Created by Daan Knoope on 4-8-2015.
 */
@ImplementedBy(ExcelServiceImpl.class)
public interface ExcelService {
	String testFunction();
	XillWorkbook loadWorkbook(String filePath, File file) throws FileNotFoundException, IllegalArgumentException, IOException, ParseException;
	String getFilePath(File file) throws IOException;
	Sheet loadSheet(Workbook workbook, String sheetName);
	int rowSize(Sheet sheet);
	int columnSize(Sheet sheet);
	String name (Sheet sheet);

}
