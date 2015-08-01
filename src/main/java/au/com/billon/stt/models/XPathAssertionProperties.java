package au.com.billon.stt.models;

/**
 * Created by Zheng on 26/07/2015.
 */
public class XPathAssertionProperties extends Properties {
    private String xPath;
    private String expectedValue;

    public String getxPath() {
        return xPath;
    }

    public void setxPath(String xPath) {
        this.xPath = xPath;
    }

    public String getExpectedValue() {
        return expectedValue;
    }

    public void setExpectedValue(String expectedValue) {
        this.expectedValue = expectedValue;
    }
}
