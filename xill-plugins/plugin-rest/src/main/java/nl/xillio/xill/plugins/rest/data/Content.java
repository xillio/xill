package nl.xillio.xill.plugins.rest.data;

import com.google.inject.ConfigurationException;
import nl.xillio.xill.api.components.ExpressionDataType;
import nl.xillio.xill.api.components.MetaExpression;
import nl.xillio.xill.api.construct.ExpressionBuilderHelper;
import nl.xillio.xill.api.data.XmlNode;
import nl.xillio.xill.api.data.XmlNodeFactory;
import nl.xillio.xill.api.errors.RobotRuntimeException;
import nl.xillio.xill.services.inject.InjectorUtils;
import nl.xillio.xill.services.json.GsonParser;
import org.apache.http.entity.ContentType;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * Support class for storing the content:
 * either for request body
 * or for the response
 */
public class Content {

	private static final Logger LOGGER = LogManager.getLogger();
	private String content = "";
	private ContentType type = ContentType.TEXT_PLAIN;
	private XmlNodeFactory xmlNodeFactory;
    private static final String ENCODING = "UTF-8";

	/**
	 * Acquire content from Xill variable
	 *
	 * @param contentVar request's body content
     * @param contentTypeVar request's body content type
	 */
	public Content(final MetaExpression contentVar, final MetaExpression contentTypeVar) {
        if (contentVar.isNull()) {
            return;
        }

		if (contentTypeVar.isNull()) {
            if ((contentVar.getType() == ExpressionDataType.OBJECT) || (contentVar.getType() == ExpressionDataType.LIST)) {
                // JSON format
                this.content = contentVar.toString();
                this.type = ContentType.create(ContentType.APPLICATION_JSON.getMimeType(), ENCODING);
            } else if (contentVar.getMeta(XmlNode.class) != null) {
                // XML format
                XmlNode xmlNode = contentVar.getMeta(XmlNode.class);
                this.content = xmlNode.getXmlContent();
                this.type = ContentType.create(ContentType.APPLICATION_XML.getMimeType(), ENCODING);
            } else {
                this.content = contentVar.getStringValue();
                this.type = ContentType.create(ContentType.TEXT_PLAIN.getMimeType(), ENCODING);
            }
            this.type.withCharset("UTF-8");
		} else {
            this.type = ContentType.create(contentTypeVar.getStringValue(), ENCODING);
            this.content = contentVar.getStringValue();
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
		if (responseContent != null) {
			this.content = responseContent.toString();
			this.type = responseContent.getType();
		}
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
	 * @return new Xill variable (JSON-&gt;OBJECT type / XML-&gt;XmlNode / other-&gt;ATOMIC string)
	 */
	public MetaExpression getMeta() {
		if (ContentType.APPLICATION_JSON.getMimeType().equals(this.getType().getMimeType())) {
			GsonParser jsonParser = new GsonParser();
			Object result = jsonParser.fromJson(this.getContent(), Object.class);
			return MetaExpression.parseObject(result);
		} else if (ContentType.APPLICATION_XML.getMimeType().equals(this.getType().getMimeType())) {
			if (xmlNodeFactory == null) {
				// We have no factory yet
				try {
					xmlNodeFactory = InjectorUtils.get(XmlNodeFactory.class);
				} catch (ConfigurationException e) {
					LOGGER.error("No binding found for XmlNodeFactory", e);
					throw new RobotRuntimeException("Did not detect the XML plugin, do you have it installed?", e);
				}
			}

			XmlNode xml;
			try {
				xml = xmlNodeFactory.fromString(this.getContent());
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
