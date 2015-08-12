package nl.xillio.xill.docgen.impl;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import nl.xillio.xill.docgen.DocumentationEntity;
import nl.xillio.xill.docgen.PropertiesProvider;
import nl.xillio.xill.docgen.data.Example;
import nl.xillio.xill.docgen.data.ExampleNode;
import nl.xillio.xill.docgen.data.Parameter;
import nl.xillio.xill.docgen.data.Reference;

/**
 * //TODO javadoc
 *
 * @author Thomas Biesaart
 * @author Ivor van der Hoog
 * @since 12-8-2015
 */
public class ConstructDocumentationEntity implements DocumentationEntity {
	private final String identity;
	private String description;
	private List<Parameter> parameters;
	private List<Example> examples;
	private List<Reference> references;
	private Set<String> searchTags;

	/**
	 * The constructor for the {@link ConstructDocumentationEntity}.
	 * Gets handed the name of the package of the construct and the name of the construct.
	 * 
	 * @param packageName
	 *        The name of the package of the construct.
	 * @param identity
	 *        The name of the identity of the construct.
	 */
	public ConstructDocumentationEntity(final String identity) {
		this.identity = identity;
	}

	@Override
	public String getIdentity() {
		return identity;
	}

	@Override
	public Map<String, Object> getProperties() {
		Map<String, Object> properties = new LinkedHashMap<>();

		properties.put("identity", identity);
		properties.put("description", description);
		properties.put("parameters", PropertiesProvider.extractContent(parameters));
		properties.put("examples", PropertiesProvider.extractContent(examples));
		properties.put("references", PropertiesProvider.extractContent(references));
		properties.put("searchTags", searchTags);

		return properties;
	}

	/**
	 * Sets the description of the {@link ConstructDocumentationEntity}
	 *
	 * @param Description
	 *        The description of the function.
	 */
	public void setDescription(final String Description) {
		description = Description;
	}
	
	/**
	 * Returns the description of the construct.
	 * @return
	 * 				The description.
	 */
	public String getDescription(){
		return description;
	}

	/**
	 * Set the {@link Parameter}(s) of the construct.
	 * 
	 * @param parameters
	 *        The parameters the construct has.
	 */
	public void setParameters(final List<Parameter> parameters) {
		this.parameters = parameters;
	}
	
	/**
	 * Returns the names of all parameters in an array.
	 * @return
	 * 			The name of all parameters.
	 */
	public List<String> getParameterNames(){
		List<String> parameterNames = new ArrayList<>();
		for(int t = 0; t < parameters.size(); ++t){
			parameterNames.add(parameters.get(t).getName());
		}
		return parameterNames;
	}

	/**
	 * Set the {@link Example}(s) of the construct.
	 * 
	 * @param examples
	 *        The example we want to construct to have.
	 */
	public void setExamples(final List<Example> examples) {
		this.examples = examples;
	}
	
	/**
	 * Returns all example code in the construct through parsing all exampleNodes in each example to see:
	 * If the exampleNode is a codeblock.
	 * Add the content of that code block.
	 * @return
	 * 				All example code in the construct.
	 */
	public List<String> getExampleCode(){
		List<String> exampleCode = new ArrayList<>();
			
		for(Example example : examples){
			for(ExampleNode node : example.getExampleNodes()){
				if(node.getTagName() == "code"){
					exampleCode.add(node.getContent());
				}
			}
		}
		
		return exampleCode;
	}

	/**
	 * Set the references of the construct.
	 * 
	 * @param references
	 *        The references we want the construct to have.
	 */
	public void setReferences(final List<Reference> references) {
		this.references = references;
	}

	/**
	 * Set the searchTags of the construct.
	 * 
	 * @param searchTags
	 *        The searchTags we want the construct to have.
	 */
	public void setSearchTags(final Set<String> searchTags) {
		this.searchTags = searchTags;
	}
	
	/**
	 * Returns all search tags in an array.
	 * @return
	 * 				All search tags.
	 */
	public List<String> getSearchTags(){
		List<String> result = new ArrayList<>();
		for(String s : searchTags){
			result.add(s);
		}
		return result;
	}

	@Override
	public String getType() {
		return "construct";
	}
}
