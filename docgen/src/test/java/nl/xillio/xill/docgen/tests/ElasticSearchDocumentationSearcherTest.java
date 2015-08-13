package nl.xillio.xill.docgen.tests;

import static org.mockito.Mockito.*;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashMap;

import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpression;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;
import javax.xml.xpath.XPath;

import org.w3c.dom.Document;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;








import nl.xillio.xill.docgen.exceptions.ParsingException;
import nl.xillio.xill.docgen.impl.ConstructDocumentationEntity;
import nl.xillio.xill.docgen.impl.ElasticsearchDocumentationSearcher;
import nl.xillio.xill.docgen.impl.XmlDocumentationParser;

import org.elasticsearch.client.Client;
import org.testng.Assert;
import org.testng.annotations.Test;

public class ElasticSearchDocumentationSearcherTest{
	
	@Test
	public void testNormalUsage(){
		Client client = mock(Client.class);
		ConstructDocumentationEntity entity = mock(ConstructDocumentationEntity.class);
		when(entity.getIdentity()).thenReturn("identity");
		when(entity.getProperties()).thenReturn(new HashMap<String, Object>());
		
		ElasticsearchDocumentationSearcher searcher = new ElasticsearchDocumentationSearcher(client);
		
		searcher.index("package", entity);
		searcher.search("search this");
		}

}
