package nl.xillio.migrationtool.documentation;

import static org.rendersnake.HtmlAttributesFactory.class_;
import static org.rendersnake.HtmlAttributesFactory.href;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.rendersnake.HtmlCanvas;

import javafx.util.Pair;

/**
 * The FunctionDocument is the java implementation of our documentation. <BR><BR>
 * 
 * It contains a name which has to be unique.
 * It contains a list of parameters which are pairs of strings (the type and the name)
 * Note that in 3.0 we will probably not have types anymore. <BR><BR>
 * 
 * It contains a string with the description.<BR>
 * 
 * It contains a list with examples which are represented as a pair of strings.<BR>
 * 
 * It contains a list with the ID's of all the FunctionDocuments it links to called links.<BR>
 * 
 * It contains a list of SearchTags which help indexing it.<BR>
 * 
 * It contains a List of applications in case the creator of the function has some specific applications he or she wants to mention. <BR>
 * 
 * The FunctionDocument is capable of generating its own HTML page with the function toHTML().
 * @author Ivor
 */
public class FunctionDocument {
    private String functionName = "", description, version, packet = "testRealm";
    private final List<Pair<String, String>> parameters = new ArrayList<>();
    private final List<Pair<String, String>> examples = new ArrayList<>();
    private final List<String> links = new ArrayList<>();
    private final List<String> searchTags = new ArrayList<>();
    private final List<String> applications = new ArrayList<>();

    /**
     * @param name
     *            The name of the function.
     */
    public void setName(final String name) {
	functionName = name;
    }

    /**
     * @return Returns the name of the function
     */
    public String getName() {
	return functionName;
    }

    /**
     * @param Description
     *            The description of the function.
     */
    public void setDescription(final String Description) {
	description = Description;
    }

    /**
     * @return Returns the description
     */
    public String getDescription() {
	return description;
    }
    
    /**
     * @param v
     * 			The version of the function
     */
    public void setVersion(String v)
    {
    	this.version = v;
    }
    
    /**
     * @return Returns the version of the functiondocument
     */
    public String getVersion()
    {
    	return this.version;
    }
    
    /**
     * Setter for the package of the functiondocument
     * @param p
     * 		The package the FunctionDocument is in
     */
    public void setPackage(String p)
    {
    	this.packet = p;
    }
    
    /**
     * @return 
     * Returns the package of the functionDocument
     * 
     */
    public String getPackage()
    {
    	return this.packet;
    }

    /**
     * @param type
     *            The parameterType.
     * @param name
     *            The name of the parameter.
     */
    public void addParameter(final String type, final String name) {
	parameters.add(new Pair<>(type, name));
    }

    /**
     * @return Returns the paramters
     */
    public List<Pair<String, String>> getParameters() {
	return parameters;
    }

    /**
     * @param action
     *            The actiontype of the item.
     * @param content
     *            The content of the item (string).
     */
    public void addExample(final String action, final String content) {
	examples.add(new Pair<>(action, content));
    }

    /**
     * @return Returns the examples
     */
    public List<Pair<String, String>> getExamples() {
	return examples;
    }

    /**
     * @param application
     *            The application we want to add to Applications.
     */
    public void addApplication(final String application) {
	applications.add(application);
    }

    /**
     * @param link
     *            The link we want to add.
     */
    public void addLink(final String link) {
	links.add(link);
    }

    /**
     * @param tag
     *            The searchTag we want to add.
     */
    public void addSearchTag(final String tag) {
	searchTags.add(tag);
    }

    /**
     * @return Returns the search tags
     */
    public List<String> getSearchTags() {
	return searchTags;
    }

    /**
     * @return returns the correct HTML to display the FunctionDocument
     * @throws IOException
     */
    public String toHTML() throws IOException {
	HtmlCanvas html = new HtmlCanvas();

	// Add the header
	html = addHeader(html);

	// Start the body
	html.body();

	// The title
	html = addTitle(html);

	// The function with parameters
	html = addFunction(html);

	// The description
	html = addDescription(html);

	// The examples
	if (!examples.isEmpty()) {
	    html = openList(html, "Examples");

	    for (Pair<String, String> example : examples) {
		html = addItemToList(html, example);

	    }
	    html = closeList(html);
	}

	// The applications
	if (!applications.isEmpty()) {
	    html = openList(html, "Applications");

	    for (String application : applications) {
		html = addItemToList(html, application);
	    }

	    html = closeList(html);
	}

	// The links
	if (!links.isEmpty()) {
	    html = openList(html, "Tags");
	    for (String link : links) {
		html = addLinkToList(html, link);
	    }
	    html = closeList(html);
	}
	html._body();
	return html.toHtml();
    }

    /**
     * @param canvas
     *            The canvas we're adding a title to.
     * @return Returns the canvas with a title.
     * @throws IOException
     */
    private HtmlCanvas addTitle(final HtmlCanvas canvas) throws IOException {
	return canvas.div(class_("Section")).h1().write(functionName)._h1()._div();
    }

    /**
     * @param canvas
     *            The canvas we're adding a header to.
     * @return Returns a canvas with a header added.
     * @throws IOException
     */
    private HtmlCanvas addHeader(final HtmlCanvas canvas) throws IOException {
	return canvas.head().title().content(functionName).macros().stylesheet("../_static/default.css")._head();
    }

    /**
     * @param canvas
     *            The canvas we're adding the function to.
     * @return Returns the canvas with a function and its parameters added.
     * @throws IOException
     */
    private HtmlCanvas addFunction(final HtmlCanvas canvas) throws IOException {
	// We build the string
	String str = "(";
	for (Pair<String, String> parameter : parameters) {
	    str += parameter.getKey() + " " + parameter.getValue() + ", ";
	}
	// Remove the last comma
	if (str.length() > 1) {
	    str = str.substring(0, str.length() - 2);
	}
	str += ")";

	// Write the function name and the parameters behind it
	return canvas.div(class_("Section")).p().strong().write(functionName)._strong().write(str)._p()._div();
    }

    /**
     * @param canvas
     *            The canvas we're adding a description to.
     * @return Returns the canvas with a description added.
     * @throws IOException
     */
    private HtmlCanvas addDescription(final HtmlCanvas canvas) throws IOException {
	return canvas.div(class_("Section")).h2().write("Description")._h2().p().write(description)._p()._div();
    }

    /**
     * @param canvas
     *            The canvas we're opening the exampleSection for.
     * @return Returns the canvas with the exampleSection opened.
     * @throws IOException
     */
    private static HtmlCanvas openList(final HtmlCanvas canvas, final String listName) throws IOException {
	return canvas.div(class_(listName)).h2().write(listName)._h2().ul();
    }

    /**
     * @param canvas
     *            The canvas we want to close the list for.
     * @return Returns the canvas with the list closed.
     * @throws IOException
     */
    private static HtmlCanvas closeList(final HtmlCanvas canvas) throws IOException {
	return canvas._ul()._div();
    }

    /**
     * @param canvas
     *            The canvas we're adding the example item to.
     * @param item
     *            The item we're adding.
     * @return Returns a canvas with the item added as a listItem.
     * @throws IOException
     */
    private static HtmlCanvas addItemToList(final HtmlCanvas canvas, final Pair<String, String> item)
	    throws IOException {
	return canvas.li().p(class_("First")).write(item.getKey())._p().div(class_("highlight")).pre()
		.write(item.getValue())._pre()._div()._li();

    }

    /**
     * @param canvas
     *            The canvas we're adding the example item to.
     * @param item
     *            The item we're adding.
     * @return Returns a canvas with the item added as a listItem.
     * @throws IOException
     */
    private static HtmlCanvas addItemToList(final HtmlCanvas canvas, final String item) throws IOException {
	return canvas.li().p(class_("First")).write(item)._p()._li();

    }

    /**
     * @param canvas
     *            The canvas we're adding the link to.
     * @param link
     *            The linker we're adding.
     * @return A canvas with the link added.
     * @throws IOException
     */
    private static HtmlCanvas addLinkToList(final HtmlCanvas canvas, final String link) throws IOException {
	return canvas.li().p().a(href("../testRealm/" + link.replace(" ", "") + ".html")).write(link)._a()._p()._li();
    }
}
