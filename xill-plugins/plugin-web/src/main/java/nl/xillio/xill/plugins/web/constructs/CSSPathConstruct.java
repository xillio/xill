package nl.xillio.xill.plugins.web.constructs;

import nl.xillio.xill.api.components.MetaExpression;
import nl.xillio.xill.api.construct.Argument;
import nl.xillio.xill.api.construct.ConstructContext;
import nl.xillio.xill.api.construct.ConstructProcessor;
import nl.xillio.xill.api.construct.ExpressionBuilderHelper;
import nl.xillio.xill.api.errors.RobotRuntimeException;
import nl.xillio.xill.plugins.web.PhantomJSConstruct;
import nl.xillio.xill.plugins.web.data.WebVariable;
import nl.xillio.xill.plugins.web.services.web.WebService;
import org.openqa.selenium.InvalidElementStateException;
import org.openqa.selenium.InvalidSelectorException;

import java.util.ArrayList;
import java.util.List;

/**
 * Select web element(s) on the page according to provided CSS Path selector
 */
public class CSSPathConstruct extends PhantomJSConstruct {

    @Override
    public String getName() {
        return "cssPath";
    }

    @Override
    public ConstructProcessor prepareProcess(final ConstructContext context) {
        return new ConstructProcessor(
                (element, csspath) -> process(element, csspath, webService),
                new Argument("element", ATOMIC),
                new Argument("csspath", ATOMIC));
    }

    /**
     * @param elementVar input variable (should be of a NODE or PAGE type)
     * @param cssPathVar string variable specifying CSS Path selector
     * @return NODE variable or list of NODE variables or null variable (according to count of selected web elements - more/1/0)
     */
    static MetaExpression process(final MetaExpression elementVar, final MetaExpression cssPathVar, final WebService webService) {

        String query = cssPathVar.getStringValue();

        if (elementVar.isNull()) {
            return NULL;
        } else if (checkNodeType(elementVar)) {
            return processSELNode(getNode(elementVar), query, webService);
        } else if (checkPageType(elementVar)) {
            return processSELNode(getPage(elementVar), query, webService);
        } else {
            throw new RobotRuntimeException("Invalid variable type. PAGE or NODE type expected!");
        }
    }

    private static MetaExpression processSELNode(final WebVariable node, final String selector, final WebService webService) {

        try {
            List<WebVariable> results = webService.findElementsWithCssPath(node, selector);

            if (results.isEmpty()) {
                return NULL;
            } else if (results.size() == 1) {
                return createNode(node, results.get(0), webService);
            } else {
                ArrayList<MetaExpression> list = new ArrayList<MetaExpression>();

                for (WebVariable element : results) {
                    list.add(createNode(node, element, webService));
                }

                return ExpressionBuilderHelper.fromValue(list);
            }
        } catch (InvalidElementStateException e) {
            throw new RobotRuntimeException("Invalid CSSPath", e);
        } catch (InvalidSelectorException e) {
            throw new RobotRuntimeException("Invalid CSSPath", e);
        }
    }

}
