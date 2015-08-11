package nl.xillio.xill.plugins.excel.dataStructures;

import nl.xillio.xill.api.errors.NotImplementedException;

/**
 * Created by Daan Knoope on 11-8-2015.
 */
public class XillWorkbookFactorySelector {

	public static XillWorkbookFactory getFactory(String filePath){
		if(filePath.endsWith(".xls"))
			return new XillLegacyWorkbookFactory();
		else if(filePath.endsWith(".xlsx"))
			return new XillModernWorkbookFactory();
		else
			throw new NotImplementedException("This extension is not supported as Excel workbook.");
	}
}
