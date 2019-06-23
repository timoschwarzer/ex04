package ex04;

import java.io.File;
import java.util.concurrent.ConcurrentLinkedQueue;

import util_ex04.RequestDeserializer;

// Soll nicht verändert werden
public class Main {

	public static void main(String[] args) {
		// Deserialize requests
		File file = new File("src/requests_mixed");
		RequestDeserializer deser = new RequestDeserializer(file);
		ConcurrentLinkedQueue<Request> requests = null;
		
		try {
			requests = deser.getRequests();
		} catch(Exception e) {
			System.out.println("Error while reading requests!");
			e.printStackTrace();
			System.exit(1);
		}
		
		// Start processing requests
		SharedDocs doc = new SharedDocs();
		doc.processRequests(requests, 4);
	}

}
