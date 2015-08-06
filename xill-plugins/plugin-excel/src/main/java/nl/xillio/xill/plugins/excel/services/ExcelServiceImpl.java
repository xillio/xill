package nl.xillio.xill.plugins.excel.services;

import com.google.inject.Singleton;
import nl.xillio.xill.api.errors.RobotRuntimeException;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.*;

/**
 * Created by Daan Knoope on 4-8-2015.
 */
@Singleton
public class ExcelServiceImpl implements ExcelService {

	@Override public String testFunction() {
		return "teststring";
	}

	@Override public Workbook loadWorkbook(String filePath, File file) throws IOException {
		Workbook workbook = null; //if loading fails, null will be added to result
		InputStream fileStream = new FileInputStream(file);
		if(filePath.endsWith(".xls"))
			workbook = new HSSFWorkbook(fileStream);
		else if (filePath.endsWith(".xlsx"))
			workbook = new XSSFWorkbook(fileStream);
		return workbook;
	}

	@Override public String getFilePath(File file) throws IOException {
		return file.getCanonicalPath().toString();
	}

	@Override public Sheet loadSheet(Workbook workbook, String sheetName) {
		return workbook.getSheet(sheetName);
	}

	@Override public int rowSize(Sheet sheet) {
		return sheet.getLastRowNum() + 1; // Added one because POI is zero indexed
	}

	@Override public int columnSize(Sheet sheet) {
		int maxColumnSize = -1; //Initialized to -1 because 1 will be added at return and minimum is zero
		for(int i = 0; i < sheet.getLastRowNum(); i++)
			if(maxColumnSize < sheet.getRow(i).getLastCellNum())
				maxColumnSize = sheet.getRow(i).getFirstCellNum();
		return maxColumnSize + 1; // Added one because POI is zero index
	}

	@Override public String name(Sheet sheet) {
		return sheet.getSheetName();
	}

}
