package nl.xillio.xill.plugins.web.constructs;

import com.google.inject.Inject;
import nl.xillio.xill.api.components.MetaExpression;
import nl.xillio.xill.api.construct.Argument;
import nl.xillio.xill.api.construct.ConstructContext;
import nl.xillio.xill.api.construct.ConstructProcessor;
import nl.xillio.xill.api.errors.RobotRuntimeException;
import nl.xillio.xill.plugins.web.PhantomJSConstruct;
import nl.xillio.xill.plugins.web.data.WebVariable;
import nl.xillio.xill.plugins.web.services.web.FileService;
import nl.xillio.xill.plugins.web.services.web.WebService;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;

/**
 * Download a content given by URL link and save it to a file
 */
public class DownloadConstruct extends PhantomJSConstruct {
    @Inject
    private FileService fileService;

    @Override
    public ConstructProcessor prepareProcess(final ConstructContext context) {
        return new ConstructProcessor(
                (url, fileName, webContext, timeout) -> process(url, fileName, webContext, timeout, webService, context),
                new Argument("url", ATOMIC),
                new Argument("filename", ATOMIC),
                new Argument("webcontext", NULL, ATOMIC),
                new Argument("timeout", NULL, ATOMIC));
    }

    /**
     * @param urlVar
     *        The URL of the link that has to be downloaded
     * @param fileNameVar
     *        A file where the downloaded content is to be stored
     * @param webContextVar
     *        An optional page or node variable that is used for take-over the cookies and use is during the download
     * @param timeoutVar
     *        The timeout in miliseconds
     * @param webService
     *        Web service
     * @param context
     *        The context of this construct
     * @return
     *        null variable
     */
    public static MetaExpression process(final MetaExpression urlVar, final MetaExpression fileNameVar, final MetaExpression webContextVar, final MetaExpression timeoutVar, final WebService webService, final ConstructContext context) {

        String url = urlVar.getStringValue();
        if (url.isEmpty()) {
            throw new RobotRuntimeException("Invalid variable value. URL is empty.");
        }

        String fileName = fileNameVar.getStringValue();
        if (fileName.isEmpty()) {
            throw new RobotRuntimeException("Invalid variable value. Filename is empty.");
        }
        File targetFile = getFile(context, fileName);

        WebVariable webContext = null;
        if (!webContextVar.isNull()) {
            if (checkPageType(webContextVar)) {
                webContext = getPage(webContextVar);
            } else if (checkNodeType(webContextVar)) {
                webContext = getNode(webContextVar);
            } else {
                throw new RobotRuntimeException("Invalid variable type. PAGE or NODE variable type expected.");
            }
        }

        int timeout = 5000; // Default timeout (in miliseconds)
        if (!timeoutVar.isNull()) {
            timeout = timeoutVar.getNumberValue().intValue();
        }

        try {
            webService.download(url, targetFile, webContext, timeout);
        } catch (MalformedURLException e) {
            throw new RobotRuntimeException("Invalid URL" , e);
        } catch (IOException e) {
            throw new RobotRuntimeException("Error during download", e);
        }

        return NULL;
    }
}
