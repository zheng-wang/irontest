package au.com.billon.stt.models;

/**
 * Created by Zheng on 18/07/2015.
 */
public class SOAPInvocationResponse {
    private String response;


    public SOAPInvocationResponse() {}

    public SOAPInvocationResponse(String response) {
        this.response = response;
    }

    public String getResponse() {
        return response;
    }

    public void setResponse(String response) {
        this.response = response;
    }
}
