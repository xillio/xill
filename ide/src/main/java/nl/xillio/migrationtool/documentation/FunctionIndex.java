package nl.xillio.migrationtool.documentation;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.rendersnake.HtmlCanvas;

/**
 * @author Ivor
 *
 */
public class FunctionIndex extends HtmlGenerator {
	List<PackageDocument> packages = new ArrayList<>();

	/**
	 * The constructor for the {@link FunctionIndex}
	 * 
	 * @param name
	 *        The name of the {@link FunctionIndex}
	 */
	public FunctionIndex(final String name) {
		setName(name);
	}

	@Override
	public String toHTML() throws IOException {
		HtmlCanvas html = new HtmlCanvas();

		html = addHeader(html);
		html.body();
		html = addTitle(html);

		html = openTable(html);
		html = addTableWithPackages(html);
		html = closeTable(html);

		html._body();
		return html.toHtml();
	}

	/**
	 * Add a {@link PackageDocument} to the {@link FunctionIndex}
	 * 
	 * @param packet
	 *        The {@link PackageDocument} we want to add.
	 */
	public void addPackageDocument(final PackageDocument packet) {
		packages.add(packet);
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
	private HtmlCanvas addTableWithPackages(HtmlCanvas canvas) throws IOException {
		for (PackageDocument packet : packages)
		{
			canvas = packet.addTableWithFunctions(canvas);
		}
		return canvas;
	}

}
