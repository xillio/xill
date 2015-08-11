package nl.xillio.xill.plugins.excel.dataStructures;

import nl.xillio.xill.api.errors.NotImplementedException;
import org.testng.annotations.Test;

import static org.testng.Assert.*;

/**
 * Created by Daan Knoope on 11-8-2015.
 */
public class XillWorkbookFactorySelectorTest {

	@Test
	public void testGetFactoryReturnsLegacy() throws Exception {
		String filePath = "adbc.xls";
		XillWorkbookFactory factory = XillWorkbookFactorySelector.getFactory(filePath);
		assertEquals(factory.getClass(), XillLegacyWorkbookFactory.class);
	}

	@Test
	public void testGetFactoryReturnsModern() throws Exception {
		String filePath = "adbc.xlsx";
		XillWorkbookFactory factory = XillWorkbookFactorySelector.getFactory(filePath);
		assertEquals(factory.getClass(), XillModernWorkbookFactory.class);
	}

	@Test(expectedExceptions = NotImplementedException.class, expectedExceptionsMessageRegExp = "This extension is not supported as Excel workbook.")
	public void testGetFactoryNotImplemented() throws Exception{
		String filePath = "xls.adbc";
		XillWorkbookFactorySelector.getFactory(filePath);
	}
}
