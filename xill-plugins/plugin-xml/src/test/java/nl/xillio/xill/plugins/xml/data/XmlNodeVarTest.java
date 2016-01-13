package nl.xillio.xill.plugins.xml.data;

import org.testng.annotations.Test;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import static org.mockito.Mockito.*;
import static org.testng.Assert.assertEquals;

/**
 * Test the {@link XmlNodeVar}.
 */
public class XmlNodeVarTest {

    /**
     * Tests the getText() method of the XmlNodeVar class.
     */
    @Test
    public void getTextTest() {
        // mock
        Node node = mock(Node.class);
        XmlNodeVar nodeVar = spy(new XmlNodeVar(node));
        when(nodeVar.getNode()).thenReturn(node);
        NodeList list = mock(NodeList.class);
        when(node.getChildNodes()).thenReturn(list);
        when(list.getLength()).thenReturn(2);

        Node nodeA = mock(Node.class);
        when(nodeA.getTextContent()).thenReturn("A");
        when(list.item(0)).thenReturn(nodeA);

        Node nodeB = mock(Node.class);
        when(nodeB.getTextContent()).thenReturn("B");
        when(list.item(1)).thenReturn(nodeB);

        // run
        String result = nodeVar.getText();

        // verify
        verify(node, times(1)).getChildNodes();
        verify(list, times(2)).item(anyInt());

        assertEquals(result, "A\nB");
    }

    /**
     * Tests the toString() method of the XmlNodeVar class for null node.
     */
    @Test
    public void toStringTestNull() {
        // mock
        XmlNodeVar nodeVar = spy(new XmlNodeVar(null));

        // run
        String result = nodeVar.toString();

        // verify
        assertEquals(result, "XML Node[null]");
    }

    /**
     * Tests the toString() method of the XmlNodeVar class for XML node.
     */
    @Test
    public void toStringTestNode() {
        // mock
        Node node = mock(Node.class);
        when(node.getNodeName()).thenReturn("root");
        XmlNodeVar xmlNodeVar = new XmlNodeVar(node);
        XmlNodeVar nodeVar = spy(xmlNodeVar);

        // run
        String result = nodeVar.toString();

        // verify
        assertEquals(result, "XML Node[root]");
    }

    /**
     * Tests the toString() method of the XmlNodeVar class for XML document.
     *
     * @throws Exception
     */
    @Test
    public void toStringTestDoc() throws Exception {
        // mock
        Node node = mock(Node.class);
        when(node.getNodeName()).thenReturn("root");
        XmlNodeVar xmlNodeVar = new XmlNodeVar("<root><a></a></root>", true);
        XmlNodeVar nodeVar = spy(xmlNodeVar);

        // run
        String result = nodeVar.toString();

        // verify
        assertEquals(result, "XML Document[first node = root]");
    }

    /**
     * Tests the getTextPreview() method of the XmlNodeVar class.
     *
     * @throws Exception
     */
    @Test
    public void getTextPreviewTest() throws Exception {
        // mock
        Node node = mock(Node.class);
        when(node.getNodeName()).thenReturn("root");
        XmlNodeVar xmlNodeVar = new XmlNodeVar("<root><a></a></root>", true);
        XmlNodeVar nodeVar = spy(xmlNodeVar);

        // run
        String result = nodeVar.getTextPreview();

        // verify
        assertEquals(result, "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n<root>\n   <a/>\n</root>\n");
    }
}