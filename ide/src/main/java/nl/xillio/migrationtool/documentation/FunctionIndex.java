package nl.xillio.migrationtool.documentation;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.apache.commons.io.FileUtils;
import org.rendersnake.HtmlCanvas;

/**
 * <p>
 * This is a representation of the index file. It contains packages.
 * </p>
 * <p>
 * The {@link FunctionIndex} can generate HTML through generating the html of each {@link PackageDocument}
 * </p>
 *
 * @author Ivor
 *
 */
public class FunctionIndex extends HtmlGenerator {
	// All the packages that are in the Index
	private final List<PackageDocument> packages = new ArrayList<>();
	// Wheter we want the HTML to display empty packages in the index or not.
	private final boolean displayEmptyPackages = false;

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
		// For each PackageDocument we add the package name and the table with its functions
		List<PackageDocument> sortedPackages = packages.stream().sorted((a, b) -> a.getName().compareTo(b.getName())).collect(Collectors.toList());
		for (PackageDocument packet : sortedPackages) {
			if (displayEmptyPackages || packet.getPackageSize() > 0) {
				canvas = canvas.tr().td().strong().write(packet.getName())._strong()._td()._tr();
				canvas = packet.addTableWithFunctions(canvas);
			}
		}
		return canvas;
	}

	/**
	 * Builds the HTML for each package and places them at the HELP_FOLDER destination /packages/packageNAME.html
	 *
	 * @param HELP_FOLDER
	 *        The folder where we store the helpfiles.
	 */
	public void buildPackageHTML(final File HELP_FOLDER) {

		for (PackageDocument p : packages) {
			try {
				FileUtils.write(
					new File(HELP_FOLDER, "packages/" + p.getName() + ".html"), p.toHTML());
			} catch (IOException e) {
				log.error("The FunctionIndex could not generate the HTML file of the " + p.getName() + " plugin.");
			}
			try {
				FileUtils.write(
					new File(HELP_FOLDER, "index.html"), toHTML());
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
}
