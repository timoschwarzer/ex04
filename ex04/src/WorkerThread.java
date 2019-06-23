import javax.xml.soap.Text;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.locks.StampedLock;

public class WorkerThread extends Thread {

    private TextContainer doc;
    private ConcurrentLinkedQueue<Request> request;

    WorkerThread(String name, TextContainer doc, ConcurrentLinkedQueue<Request> request) {
        super(name);
        this.doc = doc;
        this.request = request;

    }

    public void run() {

        getTask();
        System.out.println(this.getName() + " is here");
    }

    public void getTask() {

        StampedLock myLock = new StampedLock();
        Request task;

        long stamp;

        stamp = myLock.writeLock();

        if (this.request.isEmpty()) {
            myLock.unlockWrite(stamp);
            return;
        }


        System.out.println(request.size());
        task = this.request.poll();

        myLock.unlockWrite(stamp);


        try {

            switch (task.getReqType()) {
                case APPEND_PAR:
                    System.out.println(this.getName() + " executed task");

                    doc.appendParagraph(task.getText());
                    break;

                case APPEND_TEXT:
                    doc.appendText(task.getText(), task.getParagraphID());
                    break;

                case REPLACE:
                    doc.replaceText(task.getText(), task.getParagraphID());
                    break;

                case DELETE:
                    doc.deleteParagraph(task.getParagraphID());
                    break;

                case GET_TEXT:
                    doc.getText();
                    break;

                case MERGE:
                    doc.mergeParagraphs(task.getParagraphID());
                    break;

                case INSERT_AFTER:
                    doc.insertAfterParagraph(task.getText(), task.getParagraphID());
                    break;

                default:
                    break;
            }
        } catch (NullPointerException np) {
            System.out.println(this.getName() + " got through?");
        }
    }
}


