package nl.xillio.xill.docgen.impl;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import nl.xillio.xill.docgen.DocumentationEntity;
import nl.xillio.xill.docgen.PropertiesProvider;
import nl.xillio.xill.docgen.data.Example;
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
		return "construct";
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
	 * Set the {@link Parameter}(s) of the construct.
	 * 
	 * @param parameters
	 *        The parameters the construct has.
	 */
	public void setParameters(final List<Parameter> parameters) {
		this.parameters = parameters;
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

	@Override
	public String getType() {
		return "construct";
	}
}
