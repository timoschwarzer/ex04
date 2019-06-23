import com.sun.corba.se.spi.orbutil.threadpool.Work;

import java.util.concurrent.ConcurrentLinkedQueue;

public class test {
    public static void main(String[] args) {

        ConcurrentLinkedQueue<Request> requests = new ConcurrentLinkedQueue<>();

        Request newReq = new Request(1, Request.RequestType.APPEND_PAR, "-");
        requests.add(newReq);

        SharedDocs sd = new SharedDocs();
        sd.processRequests(requests, 6);

    }
}
