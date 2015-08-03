package nl.xillio.migrationtool.documentation;

import static org.rendersnake.HtmlAttributesFactory.href;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.Serializable;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.rendersnake.HtmlCanvas;

import javafx.util.Pair;

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
public class PackageDocument extends HtmlGenerator implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private static Logger log = LogManager.getLogger();
	private final Set<FunctionDocument> functions = new HashSet<>();
	
	public PackageDocument(File directory, String name){
		this.setName(name);
		
		for (File file : directory.listFiles())
		{
		   if (FilenameUtils.getExtension(file.getName()).equals("txt"))
		   {
			   try {
				addDescriptiveLink(new FunctionDocument(FileUtils.readFileToString(file)));
			} catch (IOException e) {
				log.error("The function " + file.getName() + " in the package: " + name + " has a corrupt .txt file.");
			}
		   }
		}
	}

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
		functions.add(docu);
	}

	/**
	 * Adds a table with all the functions in the Package and a link to them.
	 *
	 * @param canvas
	 *        The canvas we're adding the link to.
	 * @return Returns a {@link HtmlCanvas} with a section containing a table.
	 */
	public HtmlCanvas addTableWithFunctions(HtmlCanvas canvas) {
		List<FunctionDocument> sortedFunctions = functions.stream().sorted((a, b) -> a.getName().compareTo(b.getName())).collect(Collectors.toList());
		for (FunctionDocument desLink : sortedFunctions) {
			try {
				canvas = canvas.tr().td().p()
					.a(href(generateLink(new Pair<String, String>(desLink.getPackage(), desLink.getName()))));
				canvas = desLink.addFunction(canvas);
				canvas = canvas._a()._p()._td()._tr();
			} catch (IOException e) {
				log.error("The HTML for the table with functions was not correctly generated.");
			}

		}
		return canvas;
	}

	/**
	 * Returns the amount of {@link FunctionDocument} present in the {@link PackageDocument}
	 * 
	 * @return
	 * 				Returns the amount of functions in the package.
	 */
	public int getPackageSize() {
		return functions.size();
	}

}
