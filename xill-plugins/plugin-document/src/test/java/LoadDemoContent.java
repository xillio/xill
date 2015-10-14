import nl.xillio.udm.DocumentID;
import nl.xillio.udm.UDM;
import nl.xillio.udm.builders.DocumentBuilder;
import nl.xillio.udm.exceptions.PersistenceException;
import nl.xillio.udm.services.UDMService;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * This class will generate content for the udm.
 */
public class LoadDemoContent {
	private static final int BATCH_SIZE = 100;

	public static void main(String... args) throws PersistenceException {
		for (int batchNo = 0; batchNo < 100; batchNo++) {
			try (UDMService udmService = UDM.connect()) {
				List<DocumentID> docs = new ArrayList<>();

				for (int i = 0; i < BATCH_SIZE; i++) {
					DocumentID doc = buildDoc(udmService.create());
					docs.add(doc);
				}

				udmService.persist(docs);
			}
		}
	}

	static DocumentID buildDoc(DocumentBuilder builder) {
		return builder
			.contentType()
				.name("Agenda")
			.source()
				.timestamp(new Date(1437733935000L))
				.modifiedBy("Ernst")
				.action(10)
				.current()
					.version("3")
					.decorator("tridion")
						.field("id", "tcm-15-12345")
						.field("type", "component")
					.decorator("sharepoint")
						.field("path", "/iteCollection/Documents/Projects/My Project/Meetings/Agenda for the next meeting.doc")
					.decorator("document")
						.field("title", "Agenda for the next meeting")
						.field("created", "2015-07-01")
						.field("modified", "2015-07-15")
					.decorator("file")
						.field("parentpath", "C:\\temp")
						.field("name", "agenda.doc")
						.field("extension", ".doc")
						.field("size", 55071)
					.decorator("author")
						.field("userid", "dwatrou1")
						.field("firstName", "Daniel")
						.field("lastName", "Watrous")
						.field("email", "daniel.wartrous@office.com")
				.revision("1")
					.decorator("tridion")
						.field("id", "tcm-15-12345")
						.field("type", "component")
					.decorator("sharepoint")
						.field("path", "/iteCollection/Documents/Projects/My Project/Meetings/Agenda for the next meeting.doc")
					.decorator("document")
						.field("title", "Agenda for the next meeting")
						.field("created", "2015-07-01")
						.field("modified", "2015-07-1")
					.decorator("file")
						.field("parentpath", "C:\\temp")
						.field("name", "agenda.doc")
						.field("extension", ".doc")
						.field("size", 35213)
					.decorator("author")
						.field("userid", "dwatrou1")
						.field("firstName", "Daniel")
						.field("lastName", "Watrous")
						.field("email", "daniel.wartrous@office.com")
				.revision("2", 0)
					.decorator("tridion")
						.field("id", "tcm-15-12345")
						.field("type", "component")
					.decorator("sharepoint")
						.field("path", "/iteCollection/Documents/Projects/My Project/Meetings/Agenda for the next meeting.doc")
					.decorator("document")
						.field("title", "Agenda for the next meeting")
						.field("created", "2015-07-01")
						.field("modified", "2015-07-1")
					.decorator("file")
						.field("parentpath", "C:\\temp")
						.field("name", "agenda.doc")
						.field("extension", ".doc")
						.field("size", 35213)
					.decorator("author")
						.field("userid", "dwatrou1")
						.field("firstName", "Daniel")
						.field("lastName", "Watrous")
						.field("email", "daniel.wartrous@office.com")
			.target()
				.timestamp(new Date(1437733935))
				.modifiedBy("Ernst")
				.action(10)
				.current()
					.version("3")
					.decorator("tridion")
						.field("id", "tcm-15-12345")
						.field("type", "component")
					.decorator("sharepoint")
						.field("path", "/iteCollection/Documents/Projects/My Project/Meetings/Agenda for the next meeting.doc")
					.decorator("document")
						.field("title", "Agenda for the next meeting")
						.field("created", "2015-07-01")
						.field("modified", "2015-07-15")
					.decorator("file")
						.field("parentpath", "C:\\temp")
						.field("name", "agenda.doc")
						.field("extension", ".doc")
						.field("size", 55071)
					.decorator("author")
						.field("userid", "dwatrou1")
						.field("firstName", "Daniel")
						.field("lastName", "Watrous")
						.field("email", "daniel.wartrous@office.com")
				.revision("1")
					.decorator("tridion")
						.field("id", "tcm-15-12345")
						.field("type", "component")
					.decorator("sharepoint")
						.field("path", "/iteCollection/Documents/Projects/My Project/Meetings/Agenda for the next meeting.doc")
					.decorator("document")
						.field("title", "Agenda for the next meeting")
						.field("created", "2015-07-01")
						.field("modified", "2015-07-1")
					.decorator("file")
						.field("parentpath", "C:\\temp")
						.field("name", "agenda.doc")
						.field("extension", ".doc")
						.field("size", 35213)
					.decorator("author")
						.field("userid", "dwatrou1")
						.field("firstName", "Daniel")
						.field("lastName", "Watrous")
						.field("email", "daniel.wartrous@office.com")
				.revision("2")
					.decorator("tridion")
						.field("id", "tcm-15-12345")
						.field("type", "component")
					.decorator("sharepoint")
						.field("path", "/iteCollection/Documents/Projects/My Project/Meetings/Agenda for the next meeting.doc")
					.decorator("document")
						.field("title", "Agenda for the next meeting")
						.field("created", "2015-07-01")
						.field("modified", "2015-07-1")
					.decorator("file")
						.field("parentpath", "C:\\temp")
						.field("name", "agenda.doc")
						.field("extension", ".doc")
						.field("size", 35213)
					.decorator("author")
						.field("userid", "dwatrou1")
						.field("firstName", "Daniel")
						.field("lastName", "Watrous")
						.field("email", "daniel.wartrous@office.com")
			.commit();
	}
}
