package io.irontest.models.assertion;

/**
 * Created by Zheng on 27/12/2016.
 */
public class JSONPathXMLEqualAssertionVerificationResult extends AssertionVerificationResult {
    private String actualXML;

    public String getActualXML() {
        return actualXML;
    }

    public void setActualXML(String actualXML) {
        this.actualXML = actualXML;
    }
}
