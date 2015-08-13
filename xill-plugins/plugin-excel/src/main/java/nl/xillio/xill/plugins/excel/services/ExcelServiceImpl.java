package nl.xillio.xill.plugins.excel.services;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import nl.xillio.xill.plugins.excel.dataStructures.XillSheet;
import nl.xillio.xill.plugins.excel.dataStructures.XillWorkbook;
import nl.xillio.xill.plugins.excel.dataStructures.XillWorkbookFactory;
import org.apache.poi.ss.util.WorkbookUtil;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.FileAlreadyExistsException;
import java.text.ParseException;

/**
 * Created by Daan Knoope on 4-8-2015.
 */
@Singleton
public class ExcelServiceImpl implements ExcelService {

	private final XillWorkbookFactory factory;

	@Inject
	public ExcelServiceImpl(XillWorkbookFactory factory) {
		this.factory = factory;
	}

	@Override
	public XillWorkbook loadWorkbook( File file) throws ParseException, IllegalArgumentException, IOException{
		String filePath = file.getCanonicalPath();
		if(!file.exists())
			throw new FileNotFoundException("There is no file at the given path");
		if(!(filePath.endsWith(".xls") || filePath.endsWith(".xlsx")))
			throw new IllegalArgumentException("Path does not lead to an xls or xlsx Microsoft Excel file");

		return factory.loadWorkbook(file);
	}

	@Override
	public XillWorkbook createWorkbook(File file) throws IOException {
		if(file.exists())
			throw new FileAlreadyExistsException("File already exists: no new workbook has been created.");

		return factory.createWorkbook(file);
	}

	@Override public XillSheet createSheet(XillWorkbook workbook, String sheetName) {
		if(workbook == null)
			throw new NullPointerException("The provided workbook is invalid.");
		if(sheetName == null || sheetName == "")
			throw new IllegalArgumentException("No name was supplied: sheet names must be at least one character long.");
		if(sheetName.length() > 31)
			throw new IllegalArgumentException("Sheet name is too long: must be less than 32 characters.");
		if(workbook.isReadonly())
			throw new IllegalArgumentException("Workbook is read-only");

		WorkbookUtil util = new WorkbookUtil();
		try{
			util.validateSheetName(sheetName);
		}catch(IllegalArgumentException e){
			throw new IllegalArgumentException("Sheet name contains illegal characters: "
							+ "cannot contain 0x0000, 0x0003, \\, *, ?, /, [, ] and start or end with a single quote.", e);
		}
		XillSheet sheet;
		try{
			sheet = workbook.makeSheet(sheetName);
		}catch(IllegalArgumentException e){
			throw new IllegalArgumentException("There already exists a sheet with that name in the provided workbook");
		}
		return sheet;
	}

}
