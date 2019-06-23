
import java.util.concurrent.ConcurrentLinkedQueue;

public class SharedDocs {

    public void processRequests(ConcurrentLinkedQueue<Request> requests, int numThreads) {

        WorkerThread[] workers = new WorkerThread[numThreads];
        TextContainer doc = new TextContainer();


        for (int i = 0; i < numThreads; i++) {
            workers[i] = new WorkerThread("Thread " + i, doc, requests);
        }

        for (WorkerThread worker : workers) {
            worker.start();
        }

        for (WorkerThread worker : workers) {
            try {
                worker.join();
            } catch (InterruptedException e) {
                System.out.println("Worker: " + worker.getName() + "returns Error" + e.getMessage());
            }

        }


    }
}
