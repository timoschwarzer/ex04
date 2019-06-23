
import java.io.Serializable;

public class Request implements Serializable {

    private static final long serialVersionUID = 2240199589078813794L;

    public enum RequestType {
        APPEND_PAR, APPEND_TEXT, REPLACE, DELETE, GET_TEXT, MERGE, INSERT_AFTER
    }

    private int paragraphID;
    private RequestType reqType;
    private String text;

    public Request(int paragraphID, RequestType reqType, String text) {
        this.paragraphID = paragraphID;
        this.reqType = reqType;
        this.text = text;
    }

    public int getParagraphID() {
        return paragraphID;
    }

    public RequestType getReqType() {
        return reqType;
    }

    public String getText() {
        return text;
    }



}
