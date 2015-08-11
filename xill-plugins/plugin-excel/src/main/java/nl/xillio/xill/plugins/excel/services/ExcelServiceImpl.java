package nl.xillio.xill.plugins.excel.services;

import com.google.inject.Singleton;
import nl.xillio.xill.api.components.Robot;
import nl.xillio.xill.api.construct.ConstructContext;
import nl.xillio.xill.api.errors.RobotRuntimeException;
import nl.xillio.xill.plugins.excel.dataStructures.*;
import org.apache.log4j.Logger;
import org.apache.log4j.spi.RootLogger;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;

import java.io.*;
import java.nio.file.FileAlreadyExistsException;
import java.text.ParseException;
import java.util.List;

/**
 * Created by Daan Knoope on 4-8-2015.
 */
@Singleton
public class ExcelServiceImpl implements ExcelService {

	@Override public XillWorkbook loadWorkbook( File file) throws ParseException, IllegalArgumentException, IOException{
		String filePath = file.getCanonicalPath();
		if(!file.exists())
			throw new FileNotFoundException("There is no file at the given path");
		if(!(filePath.endsWith(".xls") || filePath.endsWith(".xlsx")))
			throw new IllegalArgumentException("Path does not lead to an xls or xlsx Microsoft Excel file");

		XillWorkbookFactory factory = XillWorkbookFactorySelector.getFactory(filePath);
		return factory.loadWorkbook(file);
	}

	@Override public XillWorkbook createWorkbook(File file) throws FileAlreadyExistsException, IOException {
		if(file.exists())
			throw new FileAlreadyExistsException("File already exists: no new workbook has been created.");

		String filePath = file.getCanonicalPath();
		XillWorkbookFactory factory = XillWorkbookFactorySelector.getFactory(filePath);
		return factory.createWorkbook(file);
	}

	@Override public String getFilePath(File file) throws IOException {
		return file.getCanonicalPath();
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
				maxColumnSize = sheet.getRow(i).getLastCellNum();
		return maxColumnSize + 1; // Added one because POI is zero index
	}

	@Override public String name(Sheet sheet) {
		return sheet.getSheetName();
	}

}
