package nl.xillio.xill.plugins.web.constructs;

import com.google.inject.Inject;
import nl.xillio.xill.api.components.MetaExpression;
import nl.xillio.xill.api.construct.Argument;
import nl.xillio.xill.api.construct.ConstructContext;
import nl.xillio.xill.api.construct.ConstructProcessor;
import nl.xillio.xill.api.errors.RobotRuntimeException;
import nl.xillio.xill.plugins.web.PhantomJSConstruct;
import nl.xillio.xill.plugins.web.services.web.WebService;

/**
 * Gets the HTML content of the web page.
 */
public class GetSourceConstruct extends PhantomJSConstruct {

    @Inject
    private WebService webService;

    @Override
    public ConstructProcessor prepareProcess(final ConstructContext context) {
        return new ConstructProcessor(
                page -> process(page, webService),
                new Argument("page", ATOMIC));
    }

    /**
     * @param pageVar input PAGE type variable
     * @param webService the service we're using.
     * @return HTML content of the web page
     */
    public static MetaExpression process(final MetaExpression pageVar, final WebService webService) {
        assertNotNull(pageVar, "element");

        if (!checkPageType(pageVar)) {
            throw new RobotRuntimeException("Invalid variable type. PAGE type expected!");
        }

        return fromValue(webService.getSource(getPage(pageVar)));
    }
}
