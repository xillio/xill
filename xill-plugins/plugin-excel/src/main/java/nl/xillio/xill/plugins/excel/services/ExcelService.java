package nl.xillio.xill.plugins.excel.services;

import com.google.inject.ImplementedBy;
import nl.xillio.xill.plugins.excel.dataStructures.XillSheet;
import nl.xillio.xill.plugins.excel.dataStructures.XillWorkbook;

import java.io.File;
import java.io.IOException;
import java.nio.file.FileAlreadyExistsException;
import java.text.ParseException;

/**
 * Created by Daan Knoope on 4-8-2015.
 */
@ImplementedBy(ExcelServiceImpl.class)
public interface ExcelService {
	XillWorkbook loadWorkbook(File file) throws IllegalArgumentException, IOException, ParseException;
	XillWorkbook createWorkbook(File file) throws FileAlreadyExistsException, IOException;
	XillSheet createSheet(XillWorkbook workbook, String sheetName);

}
