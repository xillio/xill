package nl.xillio.xill.plugins.excel.constructs;

import nl.xillio.xill.api.components.MetaExpression;
import nl.xillio.xill.api.construct.*;
import nl.xillio.xill.api.errors.RobotRuntimeException;
import nl.xillio.xill.plugins.excel.datastructurez.XillWorkbook;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Construct to get the name of all the sheets in the provided workbook.
 *
 * @author Daan Knoope
 */
public class GetSheetNamesConstruct extends Construct {

	/**
	 * Processes the xill code to return the name of the sheets in the provided workbook.
	 *
	 * @param workbookInput a workbook object as created by
	 *                      {@link CreateWorkbookConstruct} or {@link LoadWorkbookConstruct}
	 * @return a list of the name of all the sheets in the workbook provided
	 * @throws RobotRuntimeException when a wrong {@link XillWorkbook} (null)
	 *                               has been provided
	 */
	static MetaExpression process(MetaExpression workbookInput) {
		XillWorkbook workbook = assertMeta(workbookInput, "parameter 'workbook'",
						XillWorkbook.class, "result of loadWorkbook or createWorkbook");
		List<String> workbookNames = workbook.getSheetNames();
		List<MetaExpression> toReturn = workbookNames.stream().map(ExpressionBuilderHelper::fromValue).collect(Collectors.toList());
		return fromValue(toReturn);
	}

	@Override
	public ConstructProcessor prepareProcess(ConstructContext context) {
		return new ConstructProcessor(
						GetSheetNamesConstruct::process,
						new Argument("workbook", ATOMIC));
	}

}
