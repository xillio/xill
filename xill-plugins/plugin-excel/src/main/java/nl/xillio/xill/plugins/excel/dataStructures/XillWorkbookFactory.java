package nl.xillio.xill.plugins.excel.dataStructures;

import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

/**
 * Created by Daan Knoope on 11-8-2015.
 */
public abstract class XillWorkbookFactory {

	public abstract XillWorkbook createWorkbook(File file) throws IOException;
	protected abstract XillWorkbook loadWorkbook(InputStream stream, boolean readonly, File file) throws IOException;
	public XillWorkbook loadWorkbook(File file) throws IOException{
		InputStream fileStream = new FileInputStream(file);
		return loadWorkbook(fileStream, file.canRead(), file);
	}

}


