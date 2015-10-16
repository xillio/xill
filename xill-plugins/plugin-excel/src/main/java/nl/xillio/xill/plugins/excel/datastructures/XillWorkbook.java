package nl.xillio.xill.plugins.excel.datastructures;

import com.google.common.io.Files;
import nl.xillio.xill.api.data.MetadataExpression;
import org.apache.commons.io.FilenameUtils;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.CreationHelper;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Representation of an Excel workbook.
 * Wrapper for the Apache POI {@link XSSFWorkbook},
 * {@link HSSFWorkbook} and {@link Workbook} classes.
 *
 * @author Daan Knoope
 */
public class XillWorkbook implements MetadataExpression {
	protected Workbook workbook;
	private File file;
	private boolean readonly = false;
	private String location;
    private CellStyle dateCellStyle;
    private CellStyle dateTimeCellStyle;

	/**
	 * Constructor for the XillWorkbook class.
	 *
	 * @param workbook a {@link Workbook} object
	 * @param file     a {@link File} to the location
	 *                 where the workbook is (or will be) stored
	 * @throws IOException operations on the file do not succeed
	 */
	public XillWorkbook(Workbook workbook, File file) throws IOException {
		this.workbook = workbook;
		this.file = file;
		location = file.getCanonicalPath();
		if (file.exists())
			readonly = !file.canWrite();
		else
			readonly = false;
	}

    public CellStyle getDateCellStyle(){
        if(dateCellStyle == null){
            CreationHelper creationHelper = workbook.getCreationHelper();
            dateCellStyle = workbook.createCellStyle();
            dateCellStyle.setDataFormat(creationHelper.createDataFormat().getFormat("dd-mm-yyyy"));
        }
        return dateCellStyle;
    }

    public CellStyle getDateTimeCellStyle(){
        if(dateTimeCellStyle == null){
            CreationHelper creationHelper = workbook.getCreationHelper();
            dateTimeCellStyle = workbook.createCellStyle();
            dateTimeCellStyle.setDataFormat(creationHelper.createDataFormat().getFormat("dd-mm-yyyy hh:mm"));
        }
        return dateTimeCellStyle;
    }


	/**
	 * Returns a string with information for the debugging window
	 *
	 * @return the string for the debugging window
	 */
	public String getFileString() {
		return "Excel Workbook [" + location + "]";
	}

	/**
	 * Gets the {@link XillSheet} with the name provided
	 *
	 * @param sheetName the name of the sheet which should be retrieved
	 * @return the {@link XillSheet} which was retrieved from this workbook
	 * @throws IllegalArgumentException when the name of the sheet cannot
	 *                                  be found in this workbook
	 */
	public XillSheet getSheet(String sheetName) {
		if (workbook.getSheetIndex(sheetName) == -1)
			throw new IllegalArgumentException("Sheet cannot be found in the supplied workbook");
		return new XillSheet(workbook.getSheet(sheetName), readonly, this);
	}

	/**
	 * Creates a new sheet in this workbook
	 *
	 * @param sheetName the name of the new sheet
	 * @return the {@link XillSheet} which has been created
	 */
	public XillSheet makeSheet(String sheetName) {
		return new XillSheet(workbook.createSheet(sheetName), readonly, this);
	}

	/**
	 * Gets a list of the name of each of the sheets in this workbook
	 *
	 * @return a {@code List<String>} of names
	 */
	public List<String> getSheetNames() {
		List<String> sheetnames = new ArrayList<>();
		for (int i = 0; i < workbook.getNumberOfSheets(); i++)
			sheetnames.add(workbook.getSheetAt(i).getSheetName());
		return sheetnames;
	}

	/**
	 * Returns if this workbook is readonly
	 *
	 * @return {@code true} when this workbook is read-only, {@code false} otherwise
	 */
	public boolean isReadonly() {
		return readonly;
	}

	/**
	 * Checks if the file where this workbook is stored still
	 * exists on the storage device
	 *
	 * @return {@code true} if it still exists, {@code false} otherwise
	 */
	public boolean fileExists() {
		return file.exists();
	}

	/**
	 * Gets the location where this workbook is stored.
	 *
	 * @return a path as string
	 */
	public String getLocation() {
		return this.location;
	}

	/**
	 * Removes a sheet by its name from this workbook.
	 *
	 * @param sheetName the name of the {@link XillSheet} which should
	 *                  be deleted
	 * @return {@code true} if the sheet has been removed, an exception
	 * is thrown otherwise
	 * @throws IllegalArgumentException if the sheetname does not exist (anymore)
	 *                                  in this workbook
	 */
	public boolean removeSheet(String sheetName) {
		int sheetIndex = workbook.getSheetIndex(sheetName);
		if (sheetIndex == -1)
			throw new IllegalArgumentException("Sheet " + sheetName + " does not exist in this workbook");
		workbook.removeSheetAt(workbook.getSheetIndex(sheetName));
		return true;
	}

	public FileOutputStream getOuputStream() throws FileNotFoundException {
		return new FileOutputStream(location);
	}

	public FileOutputStream getOutputStream(File file) throws FileNotFoundException {
		return new FileOutputStream(file);
	}

	/**
	 * Saves the workbook by overwriting the existing file
	 *
	 * @throws IOException when the write operation could not succeed
	 */
	public void save() throws IOException {
		save(file);
	}

	/**
	 * Saves the workbook to the new location specified in the
	 * {@link File} object.
	 *
	 * @param file a {@link File} object containing the new
	 *             location of the Excel workbook
	 * @throws IOException when the write operation could not succeed
	 */
	public void save(File file) throws IOException {
		file.getParentFile().mkdirs();
		OutputStream outputStream = getOutputStream(file);
		workbook.write(outputStream);
		outputStream.close();
	}

	/**
	 * Creates a copy of the current workbook at a new location on the file system.
	 *
	 * @param file a {@link File} object pointing to the new location of the file
	 * @return a clone of this workbook, but written to the new location
	 * @throws IllegalArgumentException when there is a mismatch between the new extension
	 *                                  and the current extension of the workbook
	 * @throws IOException              when a file is being written to itself
	 * @throws IOException              when the IO operation could not succeed
	 */
	public XillWorkbook createCopy(File file) throws IOException {
		String extension = FilenameUtils.getExtension(file.getName());
		String currentExtension = workbook instanceof HSSFWorkbook ? "xls" : "xlsx";
		if (!(currentExtension.equals(extension)))
			throw new IllegalArgumentException("New file should have the same extension as original (" + currentExtension + ", not " + extension + ")");

		if (this.fileExists()) {
			copy(this.file, file); // Overwrite existing file
		} else {
			this.save(file); // Create new file
		}
		file.setWritable(true);
		XillWorkbookFactory factory = getFactory();
		return factory.loadWorkbook(file);
	}

	//Wrappers for unit testing
	//Classes underneath are only called to make the code testable

	void copy(File origin, File destination) throws IOException {
		Files.copy(origin, destination);
	}

	XillWorkbookFactory getFactory() {
		return new XillWorkbookFactory();
	}

}

