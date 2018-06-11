package io.irontest.models.assertion;

import io.irontest.models.NamespacePrefix;
import io.irontest.models.Properties;

import java.util.ArrayList;
import java.util.List;

public class XPathAssertionProperties extends Properties {
    private String xPath;
    private String expectedValue;
    private List<NamespacePrefix> namespacePrefixes = new ArrayList<NamespacePrefix>();

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

    public List<NamespacePrefix> getNamespacePrefixes() {
        return namespacePrefixes;
    }

    public void setNamespacePrefixes(List<NamespacePrefix> namespacePrefixes) {
        this.namespacePrefixes = namespacePrefixes;
    }
}
