package nl.xillio.xill.plugins.excel.dataStructures;

import org.apache.poi.ss.usermodel.Sheet;

/**
 * Created by Daan Knoope on 7-8-2015.
 */
public class XillSheet {
	private Sheet sheet;
	private String name;
	private int columnLength;
	private int rowLength;

	public XillSheet(Sheet sheet){
		this.sheet = sheet;
		rowLength = sheet.getLastRowNum() + 1; //Added one because POI is zero indexed
		columnLength = calculateColumnLength();
		name = sheet.getSheetName();
		int i = 0;
	}

	private int calculateColumnLength(){
		//CPU intensive, use only once, then use the columnLength property
		int maxColumnSize = 0; //Initialized to -1 because 1 will be added at return and minimum is zero
		for(int i = 0; i < rowLength; i++)
			if (sheet.getRow(i) != null &&
							maxColumnSize < sheet.getRow(i).getLastCellNum()) {
				maxColumnSize = sheet.getRow(i).getLastCellNum();
			}
		return maxColumnSize;
	}

	public int getColumnLength() {
		return columnLength;
	}

	public int getRowLength() {
		return rowLength;
	}

	public String getName() {
		return name;
	}
}
