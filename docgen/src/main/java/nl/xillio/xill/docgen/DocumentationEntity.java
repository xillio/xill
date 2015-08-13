package nl.xillio.xill.docgen;


import java.util.Comparator;

/**
 * This interface represents a piece of documentation of xill constructs
 *
 * @author Thomas Biesaart
 * @author Ivor van der Hoog
 * @since 12-8-2015
 */
public interface DocumentationEntity extends PropertiesProvider {
	Comparator<DocumentationEntity> SORT_BY_IDENTITY = new IdentitySorter();

	/**
	 * Get the identity of this name
	 *
	 * @return a single word identity that represents this entry. This would generally be a document name.
	 */
	String getIdentity();

	/**
	 * Get the type of entity
	 *
	 * @return a single word identifier that represents the entity type
	 */
	String getType();

	class IdentitySorter implements Comparator<DocumentationEntity> {

		@Override
		public int compare(DocumentationEntity o1, DocumentationEntity o2) {
			return o1.getIdentity().compareTo(o2.getIdentity());
		}
	}
}
