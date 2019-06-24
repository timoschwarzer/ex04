# ex04
homework


public class Paragraph {

    String text;

    static int counter = 0;
    int id;

    Paragraph(String text){
      this.id = counter;
      this.text = text;
      counter++;
    }


}


    public void getTask() {


        Request task;
        final ConcurrentLinkedQueue<Request> lock = request;

        synchronized (lock) {
            if (lock.isEmpty()) {
                System.out.println(this.getName() + " had no work");
                return;
            }

            task = lock.poll();

        }
        switch (task.getReqType()) {


            case APPEND_PAR:
                print(task);
                doc.appendParagraph(task.getText());
                break;
            case APPEND_TEXT:
                print(task);
                doc.appendText(task.getText(), task.getParagraphID());
                break;

            case REPLACE:
                print(task);
                doc.replaceText(task.getText(), task.getParagraphID());
                break;

            case DELETE:
                print(task);
                doc.deleteParagraph(task.getParagraphID());
                break;

            case GET_TEXT:
                print(task);
                doc.getText();
                break;

            case MERGE:
                print(task);
                doc.mergeParagraphs(task.getParagraphID());
                break;

            case INSERT_AFTER:
                print(task);
                doc.insertAfterParagraph(task.getText(), task.getParagraphID());
                break;

            default:
                System.out.println(this.getName() + " shouldn't be here!");
                break;
        }

    }

    private void print(Request task){
        System.out.println(this.getName() + " executed task: " + task.getReqType().toString());

    }
    
    public void run() {

        while(true) {
            getTask();
            try {
                Thread.sleep(100);
            } catch (InterruptedException e) {
            }

            if (request.isEmpty()) {
                break;
            }
        }

    }
