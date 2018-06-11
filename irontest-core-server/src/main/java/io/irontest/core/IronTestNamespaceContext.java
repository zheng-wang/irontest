package io.irontest.core;

import io.irontest.models.NamespacePrefix;

import javax.xml.namespace.NamespaceContext;
import java.util.Iterator;
import java.util.List;

public class IronTestNamespaceContext implements NamespaceContext {
    private List<NamespacePrefix> namespacePrefixes;

    public IronTestNamespaceContext(List<NamespacePrefix> namespacePrefixes) {
        this.namespacePrefixes = namespacePrefixes;
    }

    public String getNamespaceURI(String prefix) {
        String result = null;
        for (NamespacePrefix np: namespacePrefixes) {
            if (np.getPrefix().equals(prefix)) {
                result = np.getNamespace();
                break;
            }
        }

        return result;
    }

    public String getPrefix(String namespaceURI) {
        return null;
    }

    public Iterator getPrefixes(String namespaceURI) {
        return null;
    }
}
