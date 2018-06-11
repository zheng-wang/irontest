package io.irontest.models.assertion;

public class JSONPathXMLEqualAssertionVerificationResult extends AssertionVerificationResult {
    private String actualXML;

    public String getActualXML() {
        return actualXML;
    }

    public void setActualXML(String actualXML) {
        this.actualXML = actualXML;
    }
}
