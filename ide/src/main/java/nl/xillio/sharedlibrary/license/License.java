package nl.xillio.sharedlibrary.license;

import java.io.ByteArrayOutputStream;
import java.io.OutputStreamWriter;
import java.io.StringReader;
import java.security.KeyFactory;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.Signature;
import java.security.spec.X509EncodedKeySpec;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpression;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;

import org.apache.commons.codec.binary.Base64;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;

import nl.xillio.xill.util.settings.SettingsHandler;

public class License {

	public enum LicenseType {
		INTERNAL, DEVELOPER, PRODUCTION
	};
	public enum SoftwareModule {
		RUNTIME, IDE, SERVER, MODULE_XMTS, MODULE_XDA
	};

	private final SettingsHandler settings = SettingsHandler.getSettingsHandler();
	private static XPath xpath = XPathFactory.newInstance().newXPath();
	private static String PUBLICKEY =
					"MIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEAukYTB5r6Lvu2ogSCV9y1" +
									"Mu1U7RwVkrtduEzofc2K1APsuN3CfFLyNayNJfSriJe1Agyt4Qo7c30AXEdSz1na" +
									"yPkAG2LC/whQTWnqjiiS7evtSAOjDIhKH4Hlf9Sl3wMaMB50Cme2fCmwCUrmgLEa" +
									"LLUvbWCUZxsaHzmCA6OWrPjMZ0wU3rklyaA//zVPq9guGdOgdh4n4Tslvr5dRhS+" +
									"8HL5s88AbTofmdSHZ+7c6IFm/JA6Fk3JuXcR+MpLfYw+h5/edOWW8IN41RNw7Q9P" +
									"t7D9YGJiGd6xq3vTF08PAHQlZQFPwtq1Xj5W6v6e/nNYFPqLJygj1IiIOm85XRK0" +
									"0QIDAQAB";

	private boolean isValid = false;

	private Document license = null;
	private String company;
	private String contactname;
	private String contactemail;
	private String dateissued;
	private String dateexpires;
	private LicenseType licensetype;
	private final List<SoftwareModule> modules = new LinkedList<>();

	public License(final String licensetext) {
		init(licensetext);
	}

	// creates new License object from license file
	public License() {
		String licensetext = settings.simple().get("license", "license");
		init(licensetext);
	}

	public boolean isValid(final SoftwareModule module) {
		if (!isValid) {
			return false;
		}

		if (getLicenseType().equals(LicenseType.INTERNAL)) {
			return true;
		}

		if (isAuthenticated(module)) {
			return true;
		}

		return false;
	}

	public LicenseType getLicenseType() {
		return licensetype;
	}

	public String getLicenseName() {
		return contactname + " (" + company + ")";
	}

	private boolean isAuthenticated(final SoftwareModule module) {
		if (modules.contains(module)) {
			return true;
		}
		return false;
	}

	private void init(final String licensetext) {
		settings.simple().register("license", "license", null, "", true);
		settings.simple().register("license", "licensecheck", new SimpleDateFormat("yyyy-MM-dd'T'hh:mm:ss").format(new Date()), "", true);

		if (licensetext == null) {
			return;
		}

		// Load XML
		try {
			DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
			DocumentBuilder db = dbf.newDocumentBuilder();
			license = db.parse(new InputSource(new StringReader(licensetext)));
		} catch (Exception e) {
			return;
		}

		// Fetch all details we need.
		try {
			String details = getLicenseDetails();
			String signature = (String) xpath.evaluate("/license/signature/text()", license, XPathConstants.STRING);
			company = (String) xpath.evaluate("/license/details/company/text()", license, XPathConstants.STRING);
			contactname = (String) xpath.evaluate("/license/details/contactname/text()", license, XPathConstants.STRING);
			contactemail = (String) xpath.evaluate("/license/details/contactemail/text()", license, XPathConstants.STRING);
			dateissued = (String) xpath.evaluate("/license/details/dateissued/text()", license, XPathConstants.STRING);
			dateexpires = (String) xpath.evaluate("/license/details/dateexpires/text()", license, XPathConstants.STRING);

			String type = (String) xpath.evaluate("/license/details/licensetype/text()", license, XPathConstants.STRING);
			if (type.equals("Internal")) {
				licensetype = LicenseType.INTERNAL;
			} else if (type.equals("Developer")) {
				licensetype = LicenseType.DEVELOPER;
			} else if (type.equals("Production")) {
				licensetype = LicenseType.PRODUCTION;
			}

			modules.add(SoftwareModule.RUNTIME);
			NodeList nodes = (NodeList) xpath.evaluate("/license/details/modules//module", license, XPathConstants.NODESET);
			for (int i = 0; i < nodes.getLength(); i++) {
				String m = nodes.item(i).getTextContent();
				if (m.equals("IDE")) {
					modules.add(SoftwareModule.IDE);
				} else if (m.equals("Server")) {
					modules.add(SoftwareModule.SERVER);
				}
			}

			boolean validsig = validateSignature(details, signature);
			boolean validdate = validDate(dateexpires);
			if (validsig && validdate) {
				isValid = true;
			} else {
				/*
				 * if (!validsig)
				 * System.out.println("Invalid signature:\n" + signature + "\nDetails:\n" + details);
				 * else if (!validdate)
				 * System.out.println("Invalid expiry date: " + dateexpires);
				 */
			}

		} catch (XPathExpressionException e) {
			return;
		}

	}

	private boolean validateSignature(final String details, final String signature) {
		// System.out.println("Validating signature :\n" + signature + "\nAgainst details:\n" + details);
		try {
			// Initialise public key
			byte[] pubBytes = Base64.decodeBase64(PUBLICKEY);
			X509EncodedKeySpec publicSpec = new X509EncodedKeySpec(pubBytes);
			KeyFactory keyFactory = KeyFactory.getInstance("RSA");
			PublicKey public_key = keyFactory.generatePublic(publicSpec);

			// Initialise signature
			byte[] sigBytes = Base64.decodeBase64(signature);
			Signature rsa_signature = Signature.getInstance("SHA1withRSA");
			rsa_signature.initVerify(public_key);
			rsa_signature.update(details.getBytes());

			// Validate the payload
			return rsa_signature.verify(sigBytes);

		} catch (Exception e) {
			// System.out.println(e);
			return false;
		}
	}

	private boolean validDate(final String dateexpires) {
		Date now = new Date();

		String lastCheckDate = settings.simple().get("license", "licensecheck").toString();

		try {
			Date expiry = new SimpleDateFormat("yyyy-MM-dd").parse(dateexpires);
			if (lastCheckDate != null) {
				Date lastCheck = new SimpleDateFormat("yyyy-MM-dd'T'hh:mm:ss").parse(lastCheckDate);

				return now.after(lastCheck) && now.before(expiry);
			}
			return now.before(expiry);

		} catch (Exception e) {
			// System.out.println(e);
			return false;
		}
	}

	private String getLicenseDetails() {
		try {
			return nodeToString((Node) xpath.evaluate("/license/details", license, XPathConstants.NODE));
		} catch (Exception e) {
			// System.out.println(e);
		}
		return "";
	}

	@Override
	public String toString() {
		return nodeToString(license);
	}

	private static String nodeToString(final Node node) {

		if (node == null) {
			return "null";
		}

		// Remove whitespace textnodes so we can format things properly
		try {
			XPathFactory xpathFactory = XPathFactory.newInstance();
			XPathExpression xpathExp = xpathFactory.newXPath().compile("//text()[normalize-space(.) = '']");
			NodeList emptyTextNodes = (NodeList) xpathExp.evaluate(node, XPathConstants.NODESET);
			for (int i = 0; i < emptyTextNodes.getLength(); i++) {
				Node emptyTextNode = emptyTextNodes.item(i);
				emptyTextNode.getParentNode().removeChild(emptyTextNode);
			}
		} catch (Exception e) {
			// System.out.println(e);
		}
		node.normalize(); // Merge adjacent text-nodes, remove empty text nodes

		try {
			ByteArrayOutputStream output = new ByteArrayOutputStream();

			// Configure transformer
			Transformer transformer = TransformerFactory.newInstance().newTransformer(); // An identity transformer
			transformer.setOutputProperty(OutputKeys.METHOD, "xml");
			transformer.setOutputProperty(OutputKeys.OMIT_XML_DECLARATION, "no");
			transformer.setOutputProperty(OutputKeys.ENCODING, "UTF-8");
			transformer.setOutputProperty(OutputKeys.STANDALONE, "yes"); // Technically we don't need this attribute at all, but it forces the header to be rewritten and have the root element end up on line
																																		// 2.
			transformer.setOutputProperty(OutputKeys.INDENT, "yes");
			transformer.setOutputProperty("{http://xml.apache.org/xslt}indent-amount", "2");

			transformer.transform(new DOMSource(node), new StreamResult(new OutputStreamWriter(output, "UTF-8")));

			String result = output.toString("UTF-8");
			result = result.replaceAll("[\\s]+", " ").replace("> <", "><");
			return result;
		} catch (Exception e) {
			// System.out.println(e);
			return null;
		}

	}

	public void Sign(final PrivateKey privateKey) {
		Signature rsa_signature = null;
		byte[] signature = null;
		try {
			rsa_signature = Signature.getInstance("SHA1withRSA");
			rsa_signature.initSign(privateKey);
			rsa_signature.update(getLicenseDetails().getBytes());
			signature = rsa_signature.sign();
			String signaturetext = Base64.encodeBase64String(signature);

			Node signode = (Node) xpath.evaluate("/license/signature", license, XPathConstants.NODE);
			if (signode != null) {
				signode.setTextContent(signaturetext);
			}
		} catch (Exception e) {
			// System.out.println(e);
		}
	}

}
