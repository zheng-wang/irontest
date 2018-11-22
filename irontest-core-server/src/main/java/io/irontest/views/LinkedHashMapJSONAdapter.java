package io.irontest.views;

import com.fasterxml.jackson.databind.ObjectMapper;
import freemarker.template.*;
import io.irontest.utils.IronTestUtils;

import javax.xml.transform.TransformerException;
import java.io.IOException;
import java.util.LinkedHashMap;

/**
 * Convert entry values in the LinkedHashMap to JSON string for output to FreeMarker template.
 */
public class LinkedHashMapJSONAdapter extends WrappingTemplateModel implements TemplateHashModel {
    private LinkedHashMap map;

    public LinkedHashMapJSONAdapter(LinkedHashMap map, IronTestObjectWrapper ow) {
        super(ow);
        this.map = map;
    }

    @Override
    public TemplateModel get(String key) throws TemplateModelException {
        Object value = map.get(key);
        if (value == null) {
            return wrap(null);
        } else {
            ObjectMapper objectMapper = new ObjectMapper();
            String valueJSON;
            try {
                valueJSON = IronTestUtils.prettyPrintJSONOrXML(objectMapper.writeValueAsString(value));
            } catch (TransformerException | IOException e) {
                throw new TemplateModelException(e);
            }
            return new SimpleScalar(valueJSON);
        }
    }

    @Override
    public boolean isEmpty() {
        return map.isEmpty();
    }
}
