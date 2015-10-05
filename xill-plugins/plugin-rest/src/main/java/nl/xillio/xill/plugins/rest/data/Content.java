package nl.xillio.xill.plugins.rest.data;

import nl.xillio.xill.api.components.ExpressionDataType;
import nl.xillio.xill.api.components.MetaExpression;
import nl.xillio.xill.api.construct.ExpressionBuilderHelper;
import nl.xillio.xill.api.errors.RobotRuntimeException;
import nl.xillio.xill.api.data.XmlNode;
import nl.xillio.xill.plugins.xml.data.XmlNodeVar;
import nl.xillio.xill.services.json.GsonParser;

import org.apache.http.entity.ContentType;

/**
 * Support class for storing the content:
 *   either for request body
 *   or for the response   
 */
public class Content {

	private String content = "";
	private ContentType type;

	/**
	 * Acquire content from Xill variable 
	 * 
	 * @param contentVar request's body content
	 */
	public Content(final MetaExpression contentVar) {
		if (contentVar.isNull()) {
			return;
		}

		if ((contentVar.getType() == ExpressionDataType.OBJECT) || (contentVar.getType() == ExpressionDataType.LIST)) {
			// JSON format
			this.content = contentVar.toString();
			this.type = ContentType.APPLICATION_JSON;
		} else if (contentVar.getMeta(XmlNode.class) != null) {
			// XML format
			XmlNode xmlNode = contentVar.getMeta(XmlNode.class);
			this.type = ContentType.APPLICATION_XML;
			this.content = xmlNode.getXmlContent();
		} else {
			this.content = contentVar.getStringValue();
			this.type = ContentType.TEXT_PLAIN;
		}
	}

	/**
	 * Acquire content from string
	 * 
	 * @param text content
	 */
	public Content(final String text) {
		this.content = text;
		this.type = ContentType.TEXT_PLAIN;
	}

	/**
	 * Acquire content from Apache Fluent response
	 * 
	 * @param responseContent content
	 */
	public Content(final org.apache.http.client.fluent.Content responseContent) {
		this.content = responseContent.toString();
		this.type = responseContent.getType();
	}

	/**
	 * @return the content as a string
	 */
	public String getContent() {
		return this.content;
	}

	/**
	 * @return the type of the content
	 */
	public ContentType getType() {
		return this.type;
	}

	/**
	 * @return true if content is empty
	 */
	public boolean isEmpty() {
		return this.content.isEmpty();
	}

	/**
	 * Create the Xill variable according to the type of content and fill it with proper content 
	 * 
	 * @return new Xill variable (JSON->OBJECT type / XML->XmlNode / other->ATOMIC string)
	 */
	public MetaExpression getMeta() {
		if (ContentType.APPLICATION_JSON.getMimeType().equals(this.getType().getMimeType())) {
			GsonParser jsonParser = new GsonParser();
			Object result = jsonParser.fromJson(this.getContent(), Object.class);
			return MetaExpression.parseObject(result);
		} else if (ContentType.APPLICATION_XML.getMimeType().equals(this.getType().getMimeType())) {
			XmlNodeVar xml = null;
			try {
				xml = new XmlNodeVar(this.getContent());
			} catch (Exception e) {
				throw new RobotRuntimeException(e.getMessage());
			}
			MetaExpression result = ExpressionBuilderHelper.fromValue(xml.toString());
			result.storeMeta(XmlNode.class, xml);
			return result;
		} else {
			return ExpressionBuilderHelper.fromValue(this.content);
		}
	}
}
