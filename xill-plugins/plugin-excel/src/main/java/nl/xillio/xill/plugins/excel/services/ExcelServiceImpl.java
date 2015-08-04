package nl.xillio.xill.plugins.excel.services;

import com.google.inject.Singleton;

/**
 * Created by Daan Knoope on 4-8-2015.
 */
@Singleton
public class ExcelServiceImpl implements ExcelService {

	@Override public String testFunction() {
		return "teststring";
	}
}
