package nl.xillio.xill.plugins.document.constructs;

import com.google.inject.Inject;

import nl.xillio.udm.DocumentID;
import nl.xillio.udm.UDM;
import nl.xillio.udm.exceptions.DocumentNotFoundException;
import nl.xillio.udm.exceptions.PersistenceException;
import nl.xillio.udm.services.UDMService;
import nl.xillio.xill.api.components.MetaExpression;
import nl.xillio.xill.api.construct.Argument;
import nl.xillio.xill.api.construct.Construct;
import nl.xillio.xill.api.construct.ConstructContext;
import nl.xillio.xill.api.construct.ConstructProcessor;
import nl.xillio.xill.api.errors.RobotRuntimeException;



/**
 * 
 * Construct for removing documents or removing a version of a document.
 * if versionid is left empty the whole document will be removed otherwise only the given version.
 * 
 * @author Sander Visser
 *
 */
public class RemoveConstruct extends Construct {

	@Override
	public ConstructProcessor prepareProcess(ConstructContext context) {
		return new ConstructProcessor(
				(documentid, versionid, section) -> process(documentid, versionid, section),
				new Argument("documentid", ATOMIC),
				new Argument("versionid",fromValue("current"), ATOMIC),
				new Argument("source", fromValue("target"), ATOMIC)
		);
	}

	static MetaExpression process(MetaExpression documentid, MetaExpression versionid, MetaExpression section) {
		//get string values of arguments
		String docid = documentid.getStringValue();
		String versid = versionid.getStringValue();
		String sect = section.getStringValue();
		
		if(sect.equals("temp")){
			UDMService udm = UDM.connect();
			DocumentID doc = udm.create()
					.contentType().name("name")
					.source().current().version("2")
					.source().current().action(2)
					.target().current().action(3)
					.target().current().version("testing")
					.target().revision("whut")
					.revision("whut").version("deze").action(5)
					.revision("aarde").version("nieuwe").action(6)
					.modifiedBy("ugh")
					.commit();
			
			
			try {
				udm.persist(doc);
			} catch (PersistenceException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			return NULL;
		}
		
		//connect to UDM service
		try(UDMService udm = UDM.connect()){
			DocumentID doc = udm.get(docid);
			if(versid.equals("current")){
				udm.delete(doc);
			}else{
				if(sect.equals("source")){
					udm.document(doc).source().removeRevision(versid).commit();
				}else if (sect.equals("target")){
					udm.document(doc).target().removeRevision(versid).commit();
				}
				udm.persist(doc);
			}
			
		}catch(PersistenceException | DocumentNotFoundException e){
			throw new RobotRuntimeException(e.getMessage());
			
		}
		return NULL;
	}
}