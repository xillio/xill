package nl.xillio.xill.plugins.excel.datastructures;

import nl.xillio.xill.api.errors.NotImplementedException;
import org.apache.commons.io.FilenameUtils;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.*;

/**
 * Created by Daan Knoope on 11-8-2015.
 */

public class XillWorkbookFactory {

	//For unit testing
	FileInputStream getStream(File file) throws FileNotFoundException {
		return new FileInputStream(file);
	}

	public XillWorkbook loadWorkbook(File file) throws IOException {
		FileInputStream stream = getStream(file);
		Workbook workbook;
		switch (FilenameUtils.getExtension(file.getName())) {
			case "xls":
				workbook = makeLegacyWorkbook(stream);
				break;
			case "xlsx":
				workbook = makeModernWorkbook(stream);
				break;
			default:
				throw new NotImplementedException("This extension is not supported as Excel workbook.");
		}
		return new XillWorkbook(workbook, file);
	}

	Workbook makeLegacyWorkbook(FileInputStream stream) throws InvalidObjectException {
		HSSFWorkbook workbook;
		try {
			workbook = new HSSFWorkbook(stream);
		} catch (IOException e) {
			throw new InvalidObjectException("File cannot be opened as Excel Workbook");
		}
		return workbook;
	}

	Workbook makeModernWorkbook(FileInputStream stream) throws InvalidObjectException {
		XSSFWorkbook workbook;
		try {
			workbook = new XSSFWorkbook(stream);
		} catch (IOException e) {
			throw new InvalidObjectException("File cannot be opened as Excel Workbook");
		}
		return workbook;
	}

	public XillWorkbook createWorkbook(File file) throws IOException {
		Workbook workbook;
		switch (FilenameUtils.getExtension(file.getName())) {
			case "xls":
				workbook = new HSSFWorkbook();
				break;
			case "xlsx":
				workbook = new XSSFWorkbook();
				break;
			default:
				throw new NotImplementedException("This extension is not supported as Excel workbook.");
		}
		return new XillWorkbook(workbook, file);
	}

}


