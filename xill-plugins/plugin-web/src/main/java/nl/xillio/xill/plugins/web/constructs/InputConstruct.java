package nl.xillio.xill.plugins.web.constructs;

import nl.xillio.xill.api.components.MetaExpression;
import nl.xillio.xill.api.construct.Argument;
import nl.xillio.xill.api.construct.ConstructContext;
import nl.xillio.xill.api.construct.ConstructProcessor;
import nl.xillio.xill.api.errors.RobotRuntimeException;
import nl.xillio.xill.plugins.web.PhantomJSConstruct;
import nl.xillio.xill.plugins.web.data.WebVariable;
import nl.xillio.xill.plugins.web.services.web.WebService;

/**
 * Simulates key presses on provided web element (i.e. clear and write text)
 */
public class InputConstruct extends PhantomJSConstruct {

    @Override
    public ConstructProcessor prepareProcess(final ConstructContext context) {
        return new ConstructProcessor(
                (element, text) -> process(element, text, webService),
                new Argument("element", ATOMIC),
                new Argument("text", ATOMIC));
    }

    /**
     * @param elementVar input variable (should be of a NODE type) - web element
     * @param textVar    input string variable - text to be written to web element
     * @param webService The webService we're using.
     * @return null variable
     */
    public static MetaExpression process(final MetaExpression elementVar, final MetaExpression textVar, final WebService webService) {

        if (elementVar.isNull()) {
            return NULL;
        }

        if (!checkNodeType(elementVar)) {
            throw new RobotRuntimeException("Invalid variable type. NODE type expected!");
        }
        // else

        String text = textVar.getStringValue();

        WebVariable element = getNode(elementVar);

        try {
            webService.clear(element);
            webService.sendKeys(element, text);
        } catch (Exception e) {
            throw new RobotRuntimeException("An exception occurred when trying to use the webService.", e);
        }

        return NULL;
    }

}
