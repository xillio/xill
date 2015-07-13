package nl.xillio.migrationtool.documentation;

import static org.rendersnake.HtmlAttributesFactory.href;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javafx.util.Pair;

import org.rendersnake.HtmlCanvas;

/**
 * <p>
 * The FunctionDocument is the java implementation of our documentation
 * </p>
 * <p>
 * It contains a name which has to be unique.
 * </p>
 * <p>
 * It contains a list of parameters which are pairs of strings (their name and defaultvalue)
 * <p>
 *
 * <p>
 * It contains a {@link String} with the description.
 * </p>
 *
 * <p>
 * It contains a {@link List} with examples which are represented as a pair of strings.
 * </p>
 *
 * <p>
 * It contains a {@link Set} with the ID's of all the FunctionDocuments it links to called links.
 * </p>
 *
 * <p>
 * It contains a {@link Set} of SearchTags which help indexing it.
 * </p>
 *
 * <p>
 * It contains a {@link List} of applications in case the creator of the function has some specific applications he or she wants to mention.
 * </p>
 *
 * <p>
 * The FunctionDocument is capable of generating its own HTML page with the function toHTML().
 * </p>
 *
 * @author Ivor
 */
public class FunctionDocument extends HtmlGenerator {
	private String description, version, packet = "testRealm";
	private final List<Pair<String, String>> parameters = new ArrayList<>();
	private final List<Pair<String, String>> examples = new ArrayList<>();
	private final Set<Pair<String, String>> links = new HashSet<>();
	private final Set<String> searchTags = new HashSet<>();

	/**
	 * Sets the description of the {@link FunctionDocument}
	 * 
	 * @param Description
	 *        The description of the function.
	 */
	public void setDescription(final String Description) {
		description = Description;
	}

	/**
	 * The getter for the description.
	 * 
	 * @return Returns the description of the {@link FunctionDocument}
	 */
	public String getDescription() {
		return description;
	}

	/**
	 * Sets the version of the {@link FunctionDocument}
	 *
	 * @param v
	 *        The version of the function
	 */
	public void setVersion(final String v)
	{
		version = v;
	}

	/**
	 * @return Returns the version of the {@link FunctionDocument}
	 */
	public String getVersion()
	{
		return version;
	}

	/**
	 * Sets the package of the {@link FunctionDocument}
	 *
	 * @param p
	 *        The package the FunctionDocument is in
	 */
	public void setPackage(final String p)
	{
		packet = p;
	}

	/**
	 * @return
	 *         Returns the package of the functionDocument
	 */
	public String getPackage()
	{
		return packet;
	}

	/**
	 * Adds a parameter to the {@link FunctionDocument}
	 *
	 * @param type
	 *        The parameterType.
	 * @param name
	 *        The name of the parameter.
	 */
	public void addParameter(final String type, final String name) {
		parameters.add(new Pair<>(type, name));
	}

	/**
	 * @return Returns the parameters of the {@link FunctionDocument}
	 */
	public List<Pair<String, String>> getParameters() {
		return parameters;
	}

	/**
	 * Adds an example to the list of examples in the {@link FunctionDocument}
	 *
	 * @param description
	 *        The description of the example.
	 * @param content
	 *        The content of the example.
	 */
	public void addExample(final String description, final String content) {
		examples.add(new Pair<>(description, content));
	}

	/**
	 * Generates a string which represents a link
	 * 
	 * @param link
	 *        The package and the function we're referring to.
	 * @return
	 */
	@Override
	protected String generateLink(final Pair<String, String> link) {
		return "../" + link.getKey() + "/" + link.getValue() + ".html";
	}

	/**
	 * @return Returns the examples of the {@link FunctionDocument}
	 */
	public List<Pair<String, String>> getExamples() {
		return examples;
	}

	/**
	 * Adds a link to the list of links in the {@link FunctionDocument} to a {@link FunctionDocument} in a certain package.
	 *
	 * @param packet
	 *        The package in which the function is contained.
	 * @param function
	 *        The function which we want to link to.
	 */
	public void addLink(final String packet, final String function) {
		if (packet != null) {
			links.add(new Pair<String, String>(packet.replace(" ", ""), function.replace(" ", "")));
		} else {
			links.add(new Pair<String, String>("NoPackageGiven", function.replace(" ", "")));
		}
	}

	/**
	 * Adds a searchTag to the list of searchTags in the {@link FunctionDocument}
	 *
	 * @param tag
	 *        The searchTag we want to add.
	 */
	public void addSearchTag(final String tag) {
		searchTags.add(tag);
	}

	/**
	 * @return Returns the search tags of the {@link FunctionDocument}
	 */
	public List<String> getSearchTags() {
		return new ArrayList<String>(searchTags);
	}

	@Override
	public String toHTML() throws IOException {
		HtmlCanvas html = new HtmlCanvas();

		// Add the header
		addHeader(html);

		// Start the body
		html.body();

		// The title
		addTitle(html);

		// The function with parameters
		addPackageHeader(html);
		addFunction(html);

		// The description
		addDescription(html);

		// The examples
		if (!examples.isEmpty()) {
			openList(html, "Examples");

			for (Pair<String, String> example : examples) {
				addItemToList(html, example);

			}
			closeList(html);
		}

		// The links
		if (!links.isEmpty()) {
			openList(html, "Tags");
			for (Pair<String, String> link : links) {
				addLinkToList(html, link);
			}
			closeList(html);
		}
		html._body();
		return html.toHtml();
	}

	/**
	 * @param canvas
	 *        The canvas we're adding the function to.
	 * @return Returns a {@link HtmlCanvas} with a function and its parameters added.
	 * @throws IOException
	 * 				Throws an IOException when failing to generate correct HTML
	 */
	public HtmlCanvas addFunction(final HtmlCanvas canvas) throws IOException {
		// We build the string
		String str = "(";
		for (Pair<String, String> parameter : parameters) {
			str += parameter.getKey() + " " + parameter.getValue() + ", ";
			// Remove the last comma
			if (str.length() > 1) {
				str = str.substring(0, str.length() - 2);
			}
			str += ")";
		}
		// Write the function name and the parameters behind it
		return canvas.strong().write(functionName)._strong().write(str);
	}

	/**
	 * @param canvas
	 *        The canvas we're adding the packageHeader to.
	 * @return
	 *         A {@link HtmlCanvas} with the packageHeader added.
	 * @throws IOException
	 * 					Throws an IOException when failing to generate correct HTML.
	 */
	private HtmlCanvas addPackageHeader(final HtmlCanvas canvas) throws IOException {
		return canvas.a(href(generateLink(new Pair<String, String>("packages", packet)))).write(packet + ".")._a();
	}

	/**
	 * @param canvas
	 *        The canvas we're adding a description to.
	 * @return Returns
	 *         A {@link HtmlCanvas} with a description added.
	 * @throws IOException
	 * 					Throws an IOException when failing to generate correct HTML.
	 */
	protected HtmlCanvas addDescription(final HtmlCanvas canvas) throws IOException {
		return canvas.section().h2().write("Description")._h2().p().write(description)._p()._section();
	}
}
