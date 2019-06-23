package util_ex04;

import java.io.EOFException;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.util.concurrent.ConcurrentLinkedQueue;

import ex04.Request;
// Sollte nicht verändert werden!
public class RequestDeserializer {

	File reqFile;

	public RequestDeserializer(File reqFile) {
		this.reqFile = reqFile;
	}

	public ConcurrentLinkedQueue<Request> getRequests() throws IOException, ClassNotFoundException {
		ConcurrentLinkedQueue<Request> requests = new ConcurrentLinkedQueue<>();

		try (ObjectInputStream inStream = new ObjectInputStream(new FileInputStream(reqFile))) {

			Request currentReq;
			while ((currentReq = (Request) inStream.readObject()) != null) {
				requests.offer(currentReq);
			}

		} catch(EOFException e) {
			
		} catch(Exception e) {
			throw e;
		}

		return requests;
	}

}
