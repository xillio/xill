package nl.xillio.xill.plugins.excel.services;

import com.google.inject.ImplementedBy;

import nl.xillio.xill.plugins.excel.dataStructures.XillSheet;
import nl.xillio.xill.plugins.excel.dataStructures.XillWorkbook;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.List;

/**
 * Interface for the ExcelService which contains some of the funtionality of the Excel plugin
 *
 * @author Daan Knoope
 */

@ImplementedBy(ExcelServiceImpl.class)
public interface ExcelService {
	/**
	 * Loads a workbook from file
	 *
	 * @param file a {@link File} object that points to the location of the workbook which should be loaded
	 * @return a new {@link XillWorkbook} containing the loaded Excel workbook
	 * @throws FileNotFoundException    when the file could not be located
	 * @throws IllegalArgumentException when the extension of the file is neither xls nor xlsx
	 */
	XillWorkbook loadWorkbook(File file) throws IOException;

	/**
	 * Creates a new workbook using the provided location
	 *
	 * @param file a {@link File} object pointing to the location where the new workbook should be stored
	 * @return a new {@link XillWorkbook}
	 * @throws IllegalArgumentException when the extension of the file is neither xls nor xlsx
	 * @throws IOException              when writing to the location did not succeed
	 */
	XillWorkbook createWorkbook(File file) throws IOException;

	/**
	 * Creates a new sheet in the provided workbook
	 *
	 * @param workbook  the {@link XillWorkbook} in which the new sheet should be stored
	 * @param sheetName the name of the new sheet
	 * @return the newly created {@link XillSheet}
	 * @throws NullPointerException     when the provided workbook is incorrect
	 * @throws IllegalArgumentException when the sheet name was invalid
	 * @throws IllegalArgumentException when the workbook is read-only
	 */
	XillSheet createSheet(XillWorkbook workbook, String sheetName);

	/**
	 * Removes a sheet from the provided workbook
	 *
	 * @param workbook  the workbook from which the sheet should be removed
	 * @param sheetName the name of the sheet which should be removed
	 * @throws IllegalArgumentException when the sheet's workbook is read-only
	 * @throws IllegalArgumentException when the name of the sheet could not be found in the workbook
	 */
	void removeSheet(XillWorkbook workbook, String sheetName);

	/**
	 * Removes a list of sheets from the provided workbook
	 *
	 * @param workbook   the workbook from which the sheet should be removed
	 * @param sheetNames a list of names of the sheets which should be removed
	 * @throws IllegalArgumentException when the workbook is read-only
	 * @throws IllegalArgumentException when one or more sheets do not exist in this workbook
	 */
	void removeSheets(XillWorkbook workbook, List<String> sheetNames);

	/**
	 * Saves the provided workbook to the specified location
	 *
	 * @param workbook the {@link XillWorkbook} which should be saved
	 * @param file     a {@link File} to which the XillWorkbook can be written
	 * @return the newly saved workbook
	 * @throws IOException              when the write did not succeed
	 * @throws IllegalArgumentException when the file is read-only
	 */
	XillWorkbook save(XillWorkbook workbook, File file) throws IOException;

	/**
	 * Saves the provided workbook by overwriting the old one
	 *
	 * @param workbook the{@link XillWorkbook} which should be saved
	 * @return a string containing the location of the workbook
	 * @throws IOException              when the write did not succeed
	 * @throws IllegalArgumentException when the file is read-only
	 */
	String save(XillWorkbook workbook) throws IOException;
}
