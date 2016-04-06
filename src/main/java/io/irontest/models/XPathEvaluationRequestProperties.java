package io.irontest.models;

import java.util.List;

/**
 * Created by Zheng on 1/08/2015.
 */
public class XPathEvaluationRequestProperties extends Properties {
    private List<NamespacePrefix> namespacePrefixes;

    public XPathEvaluationRequestProperties() {}

    public XPathEvaluationRequestProperties(List<NamespacePrefix> namespacePrefixes) {
        this.namespacePrefixes = namespacePrefixes;
    }

    public List<NamespacePrefix> getNamespacePrefixes() {
        return namespacePrefixes;
    }

    public void setNamespacePrefixes(List<NamespacePrefix> namespacePrefixes) {
        this.namespacePrefixes = namespacePrefixes;
    }
}
