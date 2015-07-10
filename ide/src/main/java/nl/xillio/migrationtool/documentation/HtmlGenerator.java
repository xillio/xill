package nl.xillio.migrationtool.documentation;

import static org.rendersnake.HtmlAttributesFactory.class_;
import static org.rendersnake.HtmlAttributesFactory.href;

import java.io.IOException;

import javafx.util.Pair;

import org.rendersnake.HtmlCanvas;

/**
 * An abstract class which defines what it means to be a HTML generator. <BR>
 * Has a public {@link String} toHTML which generates HTML based on the info of the object itself <BR>
 * All the other methods defined are used by child classes.
 * @author Ivor
 *
 */
public abstract class HtmlGenerator {
		protected String functionName = "";
		
		/**
		 * 
		 */
		public HtmlGenerator() {}
		
		/**
		 * @return
		 * @throws IOException 
		 */
		public abstract String toHTML() throws IOException;
		
		/**
		 * @param name
		 */
		public void setName(String name){
			functionName = name;
		}
		
		/**
		 * @return
		 */
		public String getName(){
			return functionName;
		}
		

	    /**
	     * @param canvas
	     *            The canvas we're adding a title to.
	     * @return Returns the canvas with a title.
	     * @throws IOException
	     */
	    protected HtmlCanvas addTitle(final HtmlCanvas canvas) throws IOException {
		return canvas.div(class_("Section")).h1().write(functionName)._h1()._div();
	    }

	    /**
	     * @param canvas
	     *            The canvas we're adding a header to.
	     * @return Returns a canvas with a header added.
	     * @throws IOException
	     */
	    protected HtmlCanvas addHeader(final HtmlCanvas canvas) throws IOException {
		return canvas.head().title().content(functionName).macros().stylesheet("../_static/default.css")._head();
	    }

	    /**
	     * @param canvas
	     *            The canvas we're opening the exampleSection for.
	     * @param listName 
	     * 			  The name of the list.
	     * @return Returns the canvas with the exampleSection opened.
	     * @throws IOException
	     */
	    protected static HtmlCanvas openList(final HtmlCanvas canvas, final String listName) throws IOException {
		return canvas.div(class_(listName)).h2().write(listName)._h2().ul();
	    }
	    
	    protected static HtmlCanvas openTable(final HtmlCanvas canvas) throws IOException {
	    	return canvas.table();
	    }
	    
	    protected static HtmlCanvas closeTable(final HtmlCanvas canvas) throws IOException {
	    	return canvas._table();
	    }

	    /**
	     * @param canvas
	     *            The canvas we want to close the list for.
	     * @return Returns the canvas with the list closed.
	     * @throws IOException
	     */
	    protected static HtmlCanvas closeList(final HtmlCanvas canvas) throws IOException {
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
	    protected static HtmlCanvas addItemToList(final HtmlCanvas canvas, final Pair<String, String> item)
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
	    protected static HtmlCanvas addItemToList(final HtmlCanvas canvas, final String item) {
		try {
			return canvas.li().p(class_("First")).write(item)._p()._li();
		} catch (IOException e) {
			System.out.println(item);
			e.printStackTrace();
		}
		return null;

	    }
	    
	    /**
	     * @param canvas
	     *            The canvas we're adding the link to.
	     * @param link
	     *            The linker we're adding.
	     * @return A canvas with the link added.
	     * @throws IOException
	     */
	    protected HtmlCanvas addLinkToList(final HtmlCanvas canvas, final Pair<String, String> link) throws IOException {
	    String name = "";
	    if(link.getKey() != "packages")
	    	name = link.getKey() + "." + link.getValue();
	    else
	    	name = link.getValue();
	    return canvas.li().p().a(href(generateLink(link))).write(name)._a()._p()._li();	
	    }

	    
	    
	    /**
	     * Generates a string which represents a link
	     * @param link
	     * 			The package and the function we're referring to.
	     * @return
	     */
	    protected String generateLink(Pair<String, String> link){
	    	return "../" + link.getKey() + "/" + link.getValue() + ".html";
	    }
}
