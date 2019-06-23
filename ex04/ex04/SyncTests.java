package ex04;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.Arrays;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.Queue;
import java.util.Random;
import java.util.Set;
import java.util.concurrent.ConcurrentLinkedQueue;

import org.junit.jupiter.api.Test;

import util_ex04.RequestDeserializer;

// Sollte nicht verändert werden!
class SyncTests {

	private final int PAR_COUNT = 1000;
	private final int NUM_THREADS = 4;

	@Test
	void testAppendParagraph() {
		// Setup
		File requestFile = new File("src/requests_append");
		File wordFile = new File("src/wordlist.txt");

		String resultString = readWordlist(wordFile);

		Queue<Request> requests = readRequests(requestFile);

		Queue<Request> copy = new LinkedList<>(requests);

		// Test single-threaded
		TextContainer container = new TextContainer();

		Request req;
		while ((req = copy.poll()) != null) {
			container.appendParagraph(req.getText());
		}

		assertEquals(resultString, container.getText(), "Incorrect paragraphs for append paragraph! (single-threaded)");

		// Test with 4 Threads
		ConcurrentLinkedQueue<Request> concurrentQueue = new ConcurrentLinkedQueue<Request>(requests);
		container = new TextContainer();

		Thread[] threads = new Thread[NUM_THREADS];
		for (int i = 0; i < NUM_THREADS; i++) {
			threads[i] = new AppendParagraphRunner(concurrentQueue, container);
		}

		for (Thread thread : threads) {
			thread.start();
		}

		try {
			for (Thread thread : threads) {
				thread.join();
			}
		} catch (Exception e) {
			fail("Exception while waiting for threads to finish");
		}

		// Check that each word is contained
		String[] fileTokens = resultString.split("\n"), resultTokens = container.getText().split("\n");

		// First check same amount of tokens
		assertEquals(fileTokens.length, resultTokens.length,
				"Incorrect number of paragraphs for append paragraph! (multi-threaded)");

		// Check each word
		Set<String> fileSet = new HashSet<>(Arrays.asList(fileTokens));

		assertTrue(fileSet.containsAll(Arrays.asList(resultTokens)),
				"Incorrect paragraphs for append paragraph! (multi-threaded)");
	}

	@Test
	void testAppendText() {
		// Setup
		File requestFile = new File("src/requests_append");

		Queue<Request> requests = readRequests(requestFile);

		ConcurrentLinkedQueue<Request> copy = new ConcurrentLinkedQueue<>(requests);
		TextContainer container = setupContainer(copy);

		AppendTextRunner[] threads = new AppendTextRunner[NUM_THREADS];
		for (int i = 0; i < NUM_THREADS; i++) {
			threads[i] = new AppendTextRunner(copy, container);
		}

		for (Thread thread : threads) {
			thread.start();
		}

		try {
			for (Thread thread : threads) {
				thread.join();
			}
		} catch (Exception e) {
			fail("Exception while waiting for threads to finish");
		}

		// Check that for each successful call exactly one unique token exists
		int successCount = PAR_COUNT;
		for (AppendTextRunner thread : threads) {
			successCount += thread.successCounter;
		}
		String[] tokens = container.getText().split("\n| ");
		HashSet<String> uniqueTokens = new HashSet<>(Arrays.asList(tokens));

		assertEquals(successCount, uniqueTokens.size(),
				"Append text failed: Expected " + successCount + " unique words, but got " + uniqueTokens.size());
	}

	@Test
	void testDelete() {
		// Setup
		File requestFile = new File("src/requests_append");

		Queue<Request> requests = readRequests(requestFile);

		ConcurrentLinkedQueue<Request> copy = new ConcurrentLinkedQueue<>(requests);
		TextContainer container = setupContainer(copy);
		DeleteRunner[] threads = new DeleteRunner[NUM_THREADS];
		for (int i = 0; i < NUM_THREADS; i++) {
			threads[i] = new DeleteRunner(container);
		}

		for (DeleteRunner thread : threads) {
			thread.start();
		}

		try {
			for (DeleteRunner thread : threads) {
				thread.join();
			}
		} catch (Exception e) {
			fail("Exception while waiting for threads to finish");
		}

		// Check that correct amount of paragraphs remains and no text was changed
		int remainingPars = PAR_COUNT;
		for (DeleteRunner thread : threads) {
			remainingPars -= thread.successCounter;
		}
		String[] result = container.getText().split("\n");

		HashSet<String> uniqueTokens = new HashSet<>(Arrays.asList(result));
		assertEquals(remainingPars, uniqueTokens.size(),
				"Delete failed: Unexpected number of unique paragraphs. Expected " + remainingPars + " got: "
						+ uniqueTokens.size());
	}

	@Test
	void testReplace() {
		// Setup
		File requestFile = new File("src/requests_append");

		Queue<Request> requests = readRequests(requestFile);

		ConcurrentLinkedQueue<Request> copy = new ConcurrentLinkedQueue<>(requests);
		TextContainer container = setupContainer(copy);
		Thread[] threads = new Thread[NUM_THREADS];
		for (int i = 0; i < NUM_THREADS; i++) {
			threads[i] = new ReplaceRunner(copy, container);
		}

		for (Thread thread : threads) {
			thread.start();
		}

		try {
			for (Thread thread : threads) {
				thread.join();
			}
		} catch (Exception e) {
			fail("Exception while waiting for threads to finish");
		}

		// Check that correct amount of paragraphs remains and no text was changed
		String[] result = container.getText().split("\n");

		HashSet<String> uniqueTokens = new HashSet<>(Arrays.asList(result));
		assertEquals(PAR_COUNT, uniqueTokens.size(), "Replace failed: Unexpected number of unique paragraphs. Expected "
				+ PAR_COUNT + " got: " + uniqueTokens.size());
	}

	@Test
	void testInsertAfter() {
		// Setup
		File requestFile = new File("src/requests_append");

		Queue<Request> requests = readRequests(requestFile);

		ConcurrentLinkedQueue<Request> copy = new ConcurrentLinkedQueue<>(requests);
		TextContainer container = setupContainer(copy);
		InsertRunner[] threads = new InsertRunner[NUM_THREADS];
		for (int i = 0; i < NUM_THREADS; i++) {
			threads[i] = new InsertRunner(copy, container);
		}

		for (Thread thread : threads) {
			thread.start();
		}

		try {
			for (Thread thread : threads) {
				thread.join();
			}
		} catch (Exception e) {
			fail("Exception while waiting for threads to finish");
		}
		
		int expectedPars = PAR_COUNT;
		for(InsertRunner thread : threads) {
			expectedPars += thread.successCounter;
		}
		
		// Check that for each successful call a paragraph exists
		String[] result = container.getText().split("\n");
		HashSet<String> uniqueTokens = new HashSet<>(Arrays.asList(result));
		assertEquals(expectedPars, uniqueTokens.size(), "Insert failed: Unexpected number of unique paragraphs. Expected "
				+ expectedPars + " got: " + uniqueTokens.size());
	}
	
	@Test
	void testMerge() {
		// Setup
		File requestFile = new File("src/requests_append");

		Queue<Request> requests = readRequests(requestFile);

		ConcurrentLinkedQueue<Request> copy = new ConcurrentLinkedQueue<>(requests);
		TextContainer container = setupContainer(copy);
		MergeRunner[] threads = new MergeRunner[NUM_THREADS];
		for (int i = 0; i < NUM_THREADS; i++) {
			threads[i] = new MergeRunner(container);
		}

		for (Thread thread : threads) {
			thread.start();
		}

		try {
			for (Thread thread : threads) {
				thread.join();
			}
		} catch (Exception e) {
			fail("Exception while waiting for threads to finish");
		}

		// Check that correct amount of paragraphs remains and no text was changed
		int remainingPars = PAR_COUNT;
		for (MergeRunner thread : threads) {
			remainingPars -= thread.successCounter;
		}
		String[] result = container.getText().split("\n");

		HashSet<String> uniqueTokens = new HashSet<>(Arrays.asList(result));
		assertEquals(remainingPars, uniqueTokens.size(),
				"Merge failed: Unexpected number of unique paragraphs. Expected " + remainingPars + " got: "
						+ uniqueTokens.size());
	}


	private TextContainer setupContainer(ConcurrentLinkedQueue<Request> copy) {
		// Setup fixed number of paragraphs
		TextContainer container = new TextContainer();
		for (int i = 0; i < PAR_COUNT; i++) {
			container.appendParagraph(copy.poll().getText());
		}

		return container;
	}

	private Queue<Request> readRequests(File requestFile) {
		RequestDeserializer deser = new RequestDeserializer(requestFile);

		Queue<Request> requests = null;
		try {
			requests = deser.getRequests();
		} catch (IOException e) {
			fail(e.getMessage());
		} catch (ClassNotFoundException e) {
			fail(e.getMessage());
		}

		return requests;
	}

	private String readWordlist(File wordFile) {
		String resultString = "";
		try {
			FileReader fileReader = new FileReader(wordFile);
			BufferedReader reader = new BufferedReader(fileReader);
			String read;
			while ((read = reader.readLine()) != null) {
				resultString += (read + "\n");
			}
			reader.close();
		} catch (Exception e) {
			fail(e.getMessage());
		}

		return resultString;
	}

	private class AppendParagraphRunner extends Thread {
		ConcurrentLinkedQueue<Request> reqs;
		TextContainer container;

		AppendParagraphRunner(ConcurrentLinkedQueue<Request> reqs, TextContainer container) {
			this.reqs = reqs;
			this.container = container;
		}

		public void run() {
			Request req;
			while ((req = reqs.poll()) != null) {
				container.appendParagraph(req.getText());
			}
		}
	}

	private class AppendTextRunner extends Thread {
		ConcurrentLinkedQueue<Request> reqs;
		TextContainer container;
		Random rand = new Random();
		public int successCounter = 0;

		AppendTextRunner(ConcurrentLinkedQueue<Request> reqs, TextContainer container) {
			this.reqs = reqs;
			this.container = container;
		}

		public void run() {
			Request req;
			// Append Text to random existing paragraph and count successful calls
			while ((req = reqs.poll()) != null) {
				if (container.appendText(" " + req.getText(), rand.nextInt(PAR_COUNT))) {
					successCounter++;
				}
			}
		}
	}

	private class DeleteRunner extends Thread {
		Random rand = new Random();
		public int successCounter = 0;
		TextContainer container;

		DeleteRunner(TextContainer container) {
			this.container = container;
		}

		public void run() {
			for (int i = 0; i < PAR_COUNT / 5; i++) {
				if (container.deleteParagraph(rand.nextInt(PAR_COUNT))) {
					successCounter++;
				}
			}
		}
	}

	private class ReplaceRunner extends Thread {
		Random rand = new Random();
		TextContainer container;
		ConcurrentLinkedQueue<Request> reqs;

		ReplaceRunner(ConcurrentLinkedQueue<Request> reqs, TextContainer container) {
			this.reqs = reqs;
			this.container = container;
		}

		public void run() {
			Request req;
			while ((req = reqs.poll()) != null) {
				container.replaceText(req.getText(), rand.nextInt(PAR_COUNT));
			}
		}
	}
	
	private class InsertRunner extends Thread {
		Random rand = new Random();
		TextContainer container;
		ConcurrentLinkedQueue<Request> reqs;
		public int successCounter = 0;
		
		InsertRunner(ConcurrentLinkedQueue<Request> reqs, TextContainer container) {
			this.reqs = reqs;
			this.container = container;
		}
		
		public void run() {
			Request req;
			while ((req = reqs.poll()) != null) {
				if(container.insertAfterParagraph(req.getText(), rand.nextInt(PAR_COUNT))) {
					successCounter++;
				}
			}
		}
	}
	
	private class MergeRunner extends Thread {
		Random rand = new Random();
		public int successCounter = 0;
		TextContainer container;

		MergeRunner(TextContainer container) {
			this.container = container;
		}

		public void run() {
			for (int i = 0; i < PAR_COUNT / 5; i++) {
				if (container.mergeParagraphs(rand.nextInt(PAR_COUNT - 1))) {
					successCounter++;
				}
			}
		}
	}

}
