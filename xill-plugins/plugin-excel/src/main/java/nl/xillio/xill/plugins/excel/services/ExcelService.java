package nl.xillio.xill.plugins.excel.services;

import com.google.inject.ImplementedBy;

/**
 * Created by Daan Knoope on 4-8-2015.
 */
@ImplementedBy(ExcelServiceImpl.class)
public interface ExcelService {
	String testFunction();
}
