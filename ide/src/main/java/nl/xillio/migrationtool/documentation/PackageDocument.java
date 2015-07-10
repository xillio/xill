package nl.xillio.migrationtool.documentation;

import static org.rendersnake.HtmlAttributesFactory.href;

import java.io.IOException;
import java.util.HashSet;
import java.util.Set;

import javafx.util.Pair;

import org.rendersnake.HtmlCanvas;
import org.rendersnake.tools.PrettyWriter;

/**
 * The class which represents the documentation of a package. <BR><BR>
 * 
 * Packages have a {@link Set} of {@link FunctionDocument} which are contained in the package. <BR>
 * The PackageDocument can generate HTML to display itself and its children.
 * @author Ivor
 *
 */
public class PackageDocument extends HtmlGenerator {
    private final Set<FunctionDocument> descriptiveLinks = new HashSet<>();

	@Override
	public String toHTML() throws IOException {
    	HtmlCanvas html = new HtmlCanvas(new PrettyWriter());
    	
    	html = addHeader(html);
    	html.body();
    	html = addTitle(html);
    	
    	html = openTable(html);
    	html = addLinksToTable(html);
    	html = closeTable(html);
    	
    	html._body();
    	return html.toHtml();
	}
    

    /**
     * Adds a function to the functionDocument to which it refers
     * @param docu
     */
    public void addDescriptiveLink(final FunctionDocument docu) {
    descriptiveLinks.add(docu);
    }
	
    
    protected HtmlCanvas addLinksToTable(HtmlCanvas canvas) throws IOException{
    	for(FunctionDocument desLink : descriptiveLinks)
    	{
    		canvas = canvas.tr()
    						 .td().p().a(href(generateLink(new Pair<String, String>(desLink.getPackage(), desLink.getName())))).write(desLink.getName())._a()._p()._td()
    						 ._tr()
    						 .tr().td();
    		canvas = desLink.addFunction(canvas);
    		canvas = canvas
    						 ._td()
    						 .td().p().write(desLink.getDescription())._p()._td()
    						 ._tr();
    	}
    	
    	return canvas;
    }
    

}
