package nl.xillio.migrationtool.documentation;


import java.io.IOException;
import java.util.ArrayList;
import org.rendersnake.HtmlCanvas;
import static org.rendersnake.HtmlAttributesFactory.*;



/**
 * @author Ivor
 *Better believe it
 */
public class FunctionDocument {
	private String functionName, description;
	private ArrayList<StringTuple> 	parameters;
	private ArrayList<StringTuple>  examples;
	private ArrayList<String> links, searchTags, applications; //note that applications is not yet used
	
	
	/**
	 * Create a functionDocument
	 */
	public FunctionDocument()
	{
		//To avoid getting nullpointerExceptions
		parameters = new ArrayList<StringTuple>();
		examples   = new ArrayList<StringTuple>();
		links 	   = new ArrayList<String>();
		searchTags = new ArrayList<String>();
		applications=new ArrayList<String>();
	}
	
	/**
	 * @param name
	 * The name of the function.
	 */
	public void setName(String name)
	{ 	functionName = name;}
	
	/**
	 * @return
	 * Returns the name of the function
	 */
	public String getName()
	{ return functionName;}
	
	/**
	 * @param Description
	 * The description of the function.
	 */
	public void setDescription(String Description)
	{   description = Description; }
	
	/**
	 * @return
	 * Returns the description
	 */
	public String getDescription()
	{ return this.description;}
	
	/**
	 * @param type
	 * The parameterType.
	 * @param name
	 * The name of the parameter.
	 */
	public void addParameter(String type, String name)
	{	parameters.add(new StringTuple(type, name));}
	
	/**
	 * @return
	 * Returns the paramters
	 */
	public ArrayList<StringTuple> getParameters()
	{ return this.parameters;}
	
	/**
	 * @param action
	 * The actiontype of the item.
	 * @param content
	 * The content of the item (string).
	 */
	public void addExample(String action, String content)
	{	examples.add(new StringTuple(action, content));}
	
	/**
	 * @return
	 * Returns the examples
	 */
	public ArrayList<StringTuple>  getExamples()
	{ return examples;}
	
	/**
	 * @param application
	 * The application we want to add to Applications.
	 */
	public void addApplication(String application)
	{ 	applications.add(application); }
	
	/**
	 * @param link
	 * The link we want to add.
	 */
	public void addLink(String link)
	{   links.add(link); }
	
	/**
	 * @param tag
	 * The searchTag we want to add.
	 */
	public void addSearchTag(String tag)
	{   searchTags.add(tag); }
	
	/**
	 * @return
	 * Returns the search tags
	 */
	public ArrayList<String> getSearchTags()
	{ return this.searchTags;}
	
	
	/**
	 * @return
	 * returns the correct HTML to display the FunctionDocument
	 * @throws IOException
	 */
	public String toHTML() throws IOException
	{
		HtmlCanvas html = new HtmlCanvas();
		
		//Add the header
		html = this.addHeader(html);
				
		//Start the body
		html.body();
		
		//The title
		html= this.addTitle(html);
		
		//The function with parameters
		html = this.addFunction(html);
		
		//The description
		html = this.addDescription(html);
		
		//The examples
		if(!examples.isEmpty())
		{
			html = this.openList(html, "Examples");
	
			for(StringTuple example : examples)
			{
				html = this.addItemToList(html, example);
						
			}
			html = this.closeList(html);
		}
		
		//The applications
		if(!applications.isEmpty())
		{
			html = this.openList(html, "Applications");
			
			for(String application : applications)
			{
				html = this.addItemToList(html, application);
			}
			
			html = this.closeList(html);
		}
		
		//The links
		if(!links.isEmpty())
		{
			html = this.openList(html, "Tags");
			for(String link : links)
			{
				html = this.addLinkToList(html, link);
			}
			html = this.closeList(html);
		}
		html._body();
	  return html.toHtml();
	}
	
	/**
	 * @param canvas 
	 * The canvas we're adding a title to.
	 * @return
	 * Returns the canvas with a title.
	 * @throws IOException
	 */
	private HtmlCanvas addTitle(HtmlCanvas canvas) throws IOException
	{
		return canvas
				.div(class_("Section"))
				.h1()
					.write(this.functionName)
				._h1()
			._div();
	}
	
	
	/**
	 * @param canvas
	 * The canvas we're adding a header to.
	 * @return
	 * Returns a canvas with a header added.
	 * @throws IOException
	 */
	private HtmlCanvas addHeader(HtmlCanvas canvas) throws IOException
	{
		return canvas
				.head()
				.title()
					.content(this.functionName)
					.macros()
					.stylesheet("_static/default.css")
				._head();
	}
	/**
	 * @param canvas
	 * The canvas we're adding the function to.
	 * @return
	 * Returns the canvas with a function and its parameters added. 
	 * @throws IOException
	 */
	private HtmlCanvas addFunction(HtmlCanvas canvas) throws IOException
	{
		//We build the string
		String str = "(";
		for (StringTuple parameter : this.parameters)
		{
			str += parameter.first + " " + parameter.second + ", ";
		}
		//Remove the last comma
		if(str.length() > 1)
			str = str.substring(0, str.length() - 2);
		str += ")";
		
		//Write the function name and the parameters behind it
		return canvas
				.div(class_("Section"))
						.p()
							.strong()
								.write(this.functionName)
							._strong()
							.write(str)
						._p()
				._div();
	}
	
	/**
	 * @param canvas
	 * The canvas we're adding a description to.
	 * @return
	 * Returns the canvas with a description added.
	 * @throws IOException
	 */
	private HtmlCanvas addDescription(HtmlCanvas canvas) throws IOException
	{
		return canvas
				.div(class_("Section"))
					.h2()
						.write("Description")
					._h2()
					.p()
						.write(this.description)
					._p()
				._div();
	}
	
	/**
	 * @param canvas
	 * The canvas we're opening the exampleSection for.
	 * @return
	 * Returns the canvas with the exampleSection opened.
	 * @throws IOException
	 */
	private HtmlCanvas openList(HtmlCanvas canvas, String listName) throws IOException
	{
		return canvas
				.div(class_(listName))
					.h2()
						.write(listName)
					._h2()
						.ul();
	}
	
	/**
	 * @param canvas
	 * The canvas we want to close the list for.
	 * @return
	 * Returns the canvas with the list closed.
	 * @throws IOException
	 */
	private HtmlCanvas closeList(HtmlCanvas canvas) throws IOException
	{
		return canvas
				._ul()
				._div();
	}
	
	/**
	 * @param canvas
	 * The canvas we're adding the example item to.
	 * @param item
	 * The item we're adding.
	 * @return
	 * Returns a canvas with the item added as a listItem.
	 * @throws IOException
	 */
	private HtmlCanvas addItemToList(HtmlCanvas canvas, StringTuple item) throws IOException
	{
		return canvas
				.li()
				.p(class_("First"))
					.write(item.first)
				._p()
				.div(class_("highlight"))
					.pre()
					.write(item.second)
					._pre()
				._div()
			._li();
				
	}
	
	/**
	 * @param canvas
	 * The canvas we're adding the example item to.
	 * @param item
	 * The item we're adding.
	 * @return
	 * Returns a canvas with the item added as a listItem.
	 * @throws IOException
	 */
	private HtmlCanvas addItemToList(HtmlCanvas canvas, String item) throws IOException
	{
		return canvas
				.li()
					.p(class_("First"))
						.write(item)
					._p()
				._li();
				
	}
	
	/**
	 * @param canvas
	 * The canvas we're adding the link to.
	 * @param link
	 * The linker we're adding.
	 * @return
	 * A canvas with the link added.
	 * @throws IOException
	 */
	private HtmlCanvas addLinkToList(HtmlCanvas canvas, String link) throws IOException
	{
		return canvas
				.li()
					.p()
						.a(href(link + ".html"))
							.write(link)
						._a()
					._p()
				._li();
	}
}
