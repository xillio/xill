package nl.xillio.migrationtool.documentation;

import static org.rendersnake.HtmlAttributesFactory.*;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.rendersnake.HtmlCanvas;

import javafx.util.Pair;

/**
 * An abstract class which defines what it means to be a HTML generator. <BR>
 * Has a public {@link String} toHTML which generates HTML based on the info of
 * the object itself <BR>
 * All the other methods defined are used by child classes.
 *
 * @author Ivor
 *
 */
public abstract class HtmlGenerator {
	private String functionName = "";

	/**
	 * The constructor of the {@link HtmlGenerator}
	 */
	public HtmlGenerator() {}

	/**
	 * This function generates an HTML file based on the content of the object.
	 *
	 * @return Returns a HTML file as a {@link String}
	 * @throws IOException
	 *         Throws an IOException when failing to parse the {@link HtmlCanvas}
	 */
	public abstract String toHTML() throws IOException;

	/**
	 * Set the name of the {@link HtmlGenerator}
	 *
	 * @param name
	 *        The name of the generator.
	 */
	public void setName(final String name) {
		functionName = name;
	}

	/**
	 * Returns the name of the {@link HtmlGenerator}
	 *
	 * @return The name of the generator
	 */
	public String getName() {
		return functionName;
	}

	/**
	 * @param canvas
	 *        The canvas we're adding a title to.
	 * @return Returns A {@link HtmlCanvas} with a title added.
	 * @throws IOException
	 *         Throws an IOException when failing to generate correct HTML.
	 */
	protected HtmlCanvas addTitle(final HtmlCanvas canvas) throws IOException {
		return canvas.section().h1().write(functionName)._h1()._section();
	}

	/**
	 * @param canvas
	 *        The canvas we're adding a header to.
	 * @return Returns A {@link HtmlCanvas} with a header added.
	 * @throws IOException
	 *         Throws an IOException when failing to generate correct HTML.
	 */
	protected HtmlCanvas addHeader(final HtmlCanvas canvas) throws IOException {
		return canvas.head().title().content(functionName).macros().stylesheet(new File(DocumentationGenerator.HELP_FOLDER, "/style/style.css").toURI().toURL().toExternalForm())._head();
	}

	/**
	 * @param canvas
	 *        The canvas we're opening the exampleSection for.
	 * @param listName
	 *        The name of the list.
	 * @return Returns A {@link HtmlCanvas} with the exampleSection opened.
	 * @throws IOException
	 *         Throws an IOException when failing to generate correct HTML.
	 */
	protected static HtmlCanvas openList(final HtmlCanvas canvas, final String listName) throws IOException {
		return canvas.section().h2().write(listName)._h2().ul();
	}

	/**
	 * @param canvas
	 *        The canvas we want to close the list for.
	 * @return Returns A {@link HtmlCanvas} canvas with the list closed.
	 * @throws IOException
	 *         Throws an IOException when failing to generate correct HTML.
	 */
	protected static HtmlCanvas closeList(final HtmlCanvas canvas) throws IOException {
		return canvas._ul()._section();
	}

	/**
	 * Opens a table on the {@link HtmlCanvas}
	 *
	 * @param canvas
	 *        The canvas we're opening the table on.
	 * @return A {@link HtmlCanvas} with table opened.
	 * @throws IOException
	 *         Throws an IOException when failing to generate correct HTML.
	 */
	protected static HtmlCanvas openTable(final HtmlCanvas canvas) throws IOException {
		return canvas.section().table();
	}

	/**
	 * Closes a table on the {@link HtmlCanvas}
	 *
	 * @param canvas
	 *        The canvas we're closing the table on.
	 * @return A {@link HtmlCanvas} with table closed.
	 * @throws IOException
	 *         Throws an IOException when failing to generate correct HTML.
	 */
	protected static HtmlCanvas closeTable(final HtmlCanvas canvas) throws IOException {
		return canvas._table()._section();
	}

	/**
	 * @param canvas
	 *        The canvas we're adding the example item to.
	 * @param item
	 *        The item we're adding.
	 * @return Returns A {@link HtmlCanvas} canvas with the item added as a
	 *         listItem.
	 * @throws IOException
	 *         Throws an IOException when failing to generate correct HTML.
	 */
	protected static HtmlCanvas addItemToList(HtmlCanvas canvas, final Pair<String, String> item) throws IOException {
		canvas = canvas.li().p(class_("First")).write(item.getKey())._p().div(class_("highlight")).pre();
		String[] content = item.getValue().split("\n");
		canvas = canvas.write(content[0]);
		for (int t = 1; t < content.length; ++t) {
			canvas = canvas.br().write(content[t].trim());
		}
		return canvas._pre()._div()._li();
	}

	/**
	 * @param canvas
	 *        The canvas we're adding the example item to.
	 * @param item
	 *        The item we're adding.
	 * @return
	 * 				Returns A {@link HtmlCanvas} canvas with the item added as a listItem.
	 * @throws IOException
	 *         Throws an IOException when failing to generate correct HTML.
	 */
	protected static HtmlCanvas addItemToList(final HtmlCanvas canvas, final String item) {
		try {
			return canvas.li().p(class_("First")).write(item)._p()._li();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return null;

	}

	/**
	 * @param canvas
	 *        The canvas we're adding the link to.
	 * @param link
	 *        The linker we're adding.
	 * @return A {@link HtmlCanvas} with the link added.
	 * @throws IOException
	 *         Throws an IOException when failing to generate correct HTML.
	 */
	protected HtmlCanvas addLinkToList(final HtmlCanvas canvas, final Pair<String, String> link) throws IOException {
		String name = "";
		if (link.getKey() != "packages") {
			name = link.getKey() + "." + link.getValue();
		} else {
			name = link.getValue();
		}
		return canvas.li().p().a(href(generateLink(link))).write(name)._a()._p()._li();
	}

	/**
	 * Generates a string which represents a path of a link.
	 *
	 * @param link
	 *        The package and the function we're referring to.
	 * @return Returns a string which represents a path to where we link to.
	 */
	protected String generateLink(final Pair<String, String> link) {
		try {
			return new File(DocumentationGenerator.HELP_FOLDER, link.getKey() + "/" + link.getValue() + ".html").toURI().toURL().toExternalForm();
		} catch (MalformedURLException e) {
			e.printStackTrace();
		}
		return "";
	}
}
