package nl.xillio.xill.plugins.excel.dataStructures;

import nl.xillio.xill.api.errors.NotImplementedException;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InvalidObjectException;

/**
 * Created by Daan Knoope on 11-8-2015.
 */

public class XillWorkbookFactory {
	public XillWorkbook loadWorkbook(File file) throws IOException {
		String filePath = file.getCanonicalPath();
		FileInputStream stream = new FileInputStream(file);
		Workbook workbook = null;

		try {
			if (filePath.endsWith(".xls"))
				workbook = new HSSFWorkbook(stream);
			else if (filePath.endsWith(".xlsx"))
				workbook = new XSSFWorkbook(stream);
		} catch (IOException e) {
			throw new InvalidObjectException("File cannot be opened as Excel Workbook");
		}
		if (workbook == null)
			throw new NotImplementedException("This extension is not supported as Excel workbook.");
		return new XillWorkbook(workbook, file);
	}

	public XillWorkbook createWorkbook(File file) throws IOException {
		String filePath = file.getCanonicalPath();
		Workbook workbook;
		if (filePath.endsWith("xls"))
			workbook = new HSSFWorkbook();
		else if (filePath.endsWith(".xlsx"))
			workbook = new XSSFWorkbook();
		else
			throw new NotImplementedException("This extension is not supported as Excel workbook.");
		return new XillWorkbook(workbook, file);
	}

}


