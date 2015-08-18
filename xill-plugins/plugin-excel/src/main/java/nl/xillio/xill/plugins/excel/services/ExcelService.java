package nl.xillio.xill.plugins.excel.services;

import com.google.inject.ImplementedBy;
import nl.xillio.xill.plugins.excel.datastructures.XillSheet;
import nl.xillio.xill.plugins.excel.datastructures.XillWorkbook;

import java.io.File;
import java.io.IOException;
import java.util.List;

/**
 * Created by Daan Knoope on 4-8-2015.
 */
@ImplementedBy(ExcelServiceImpl.class)
public interface ExcelService {
	XillWorkbook loadWorkbook(File file) throws IOException;

	XillWorkbook createWorkbook(File file) throws IOException;

	XillSheet createSheet(XillWorkbook workbook, String sheetName);

	void removeSheet(XillWorkbook workbook, String sheetName);

	void removeSheets(XillWorkbook workbook, List<String> sheetNames);

	String save(File file, XillWorkbook workbook) throws IOException;

	String save(XillWorkbook workbook) throws IOException;
}
