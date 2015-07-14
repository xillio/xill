package nl.xillio.migrationtool.documentation;

import static org.rendersnake.HtmlAttributesFactory.href;

import java.io.IOException;
import java.util.HashSet;
import java.util.Set;

import javafx.util.Pair;

import org.rendersnake.HtmlCanvas;

/**
 * <p>
 * The class which represents the documentation of a package.
 * </p>
 * <p>
 * Packages have a {@link Set} of {@link FunctionDocument} which are contained in the package.
 * </p>
 * <p>
 * The PackageDocument can generate HTML to display itself.
 * </p>
 *
 * @author Ivor
 *
 */
public class PackageDocument extends HtmlGenerator {
	private final Set<FunctionDocument> descriptiveLinks = new HashSet<>();

	@Override
	public String toHTML() throws IOException {
		HtmlCanvas html = new HtmlCanvas();

		html = addHeader(html);
		html.body();
		html = addTitle(html);

		html = openTable(html);
		html = addTableWithFunctions(html);
		html = closeTable(html);

		html._body();
		return html.toHtml();
	}

	/**
	 * Adds a function to the Set of FunctionDocuments in the package.
	 *
	 * @param docu
	 *        The function we're adding.
	 */
	public void addDescriptiveLink(final FunctionDocument docu) {
		descriptiveLinks.add(docu);
	}

	/**
	 * Adds a table with all the functions in the Package and a link to them.
	 *
	 * @param canvas
	 *        The canvas we're adding the link to.
	 * @return Returns a {@link HtmlCanvas} with a section containing a table.
	 * @throws IOException
	 *         Throws an IOException when an error is encountered when
	 *         generating the HTML
	 */
	public HtmlCanvas addTableWithFunctions(HtmlCanvas canvas) throws IOException {
		for (FunctionDocument desLink : descriptiveLinks) {

			canvas = canvas.tr().td();
			canvas = desLink.addFunction(canvas);
			canvas = canvas._td()._tr();
			canvas = canvas.tr().td().p()
				.a(href(generateLink(new Pair<String, String>(desLink.getPackage(), desLink.getName()))))
				.write(desLink.getName())._a()._p()._td()._tr().tr();
			canvas = canvas.td().p().write(desLink.getDescription())._p()._td()._tr();
		}
		return canvas;
	}

}
