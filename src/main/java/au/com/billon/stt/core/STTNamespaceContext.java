package au.com.billon.stt.core;

import au.com.billon.stt.models.NamespacePrefix;

import javax.xml.XMLConstants;
import javax.xml.namespace.NamespaceContext;
import java.util.Iterator;
import java.util.List;

/**
 * Created by Zheng on 1/08/2015.
 */
public class STTNamespaceContext implements NamespaceContext {
    private List<NamespacePrefix> namespacePrefixes;

    public STTNamespaceContext(List<NamespacePrefix> namespacePrefixes) {
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
