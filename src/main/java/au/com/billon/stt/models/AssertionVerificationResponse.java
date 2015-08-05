package au.com.billon.stt.models;

/**
 * Created by Zheng on 2/08/2015.
 */
public class AssertionVerificationResponse {
    private boolean successful;

    //  If the verification is successful, result might contain actualValue such as for xpath assertion verification.
    //  If the verification fails, result might contain error messages such as for xpath assertion verification
    //  May need to refactor to be Properties when new type of assertion needs to be verified.
    private String result;

    public boolean isSuccessful() {
        return successful;
    }

    public void setSuccessful(boolean successful) {
        this.successful = successful;
    }

    public String getResult() {
        return result;
    }

    public void setResult(String result) {
        this.result = result;
    }
}
