package nl.xillio.xill.plugins.excel.datastructures;

import nl.xillio.xill.api.errors.NotImplementedException;
import org.apache.commons.io.FilenameUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.*;

/**
 * Factory to create the correct {@link XillWorkbook}
 *
 * @author Daan Knoope
 */

public class XillWorkbookFactory {

	private static final Logger LOGGER = LogManager.getLogger(XillWorkbookFactory.class);

	//For unit testing
	FileInputStream getStream(File file) throws FileNotFoundException {
		return new FileInputStream(file);
	}

	/**
	 * Creates a XillWorkbook from a path to a valid existing Excel workbook.
	 *
	 * @param file a {@link File} object pointing to the existing Excel workbook file
	 * @return the loaded {@link XillWorkbook}
	 * @throws IOException             when the system could not load the file
	 * @throws NotImplementedException when an extension other than xls or xlsx has been used
	 */
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

	/**
	 * Wraps the Apache POI HSSFWorkbook (xls) in a XillWorkbook
	 *
	 * @param stream a {@link FileInputStream} to the workbook which should be loaded
	 * @return the XillWorkbook
	 * @throws InvalidObjectException when the file cannot be opened
	 */
	Workbook makeLegacyWorkbook(FileInputStream stream) throws InvalidObjectException {
		HSSFWorkbook workbook;
		try {
			workbook = new HSSFWorkbook(stream);
		} catch (IOException e) {
			LOGGER.error(e.getMessage(), e);
			throw new InvalidObjectException("File cannot be opened as Excel Workbook");
		}
		return workbook;
	}

	/**
	 * Wraps the Apache POI XSSFWorkbook (xlsx) in a XillWorkbook
	 *
	 * @param stream a {@link FileInputStream} to the workbook which should be loaded
	 * @return the XillWorkbook
	 * @throws InvalidObjectException when the file cannot be loaded
	 */
	Workbook makeModernWorkbook(FileInputStream stream) throws InvalidObjectException {
		XSSFWorkbook workbook;
		try {
			workbook = new XSSFWorkbook(stream);
		} catch (IOException e) {
			LOGGER.error(e.getMessage(), e);
			throw new InvalidObjectException("File cannot be opened as Excel Workbook");
		}
		return workbook;
	}

	/**
	 * Creates a fresh XillWorkbook at the specified path
	 *
	 * @param file a {@link File} with the path where the new file should be located
	 * @return the new XillWorkbook
	 * @throws IOException             when the file could not be created
	 * @throws NotImplementedException when an extension other than xls or xlsx has been used
	 */
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
				throw new NotImplementedException("This extension is not supported as Excel workbook");
		}
		return new XillWorkbook(workbook, file);
	}

}


