package io.irontest.views;

import freemarker.template.DefaultObjectWrapper;
import freemarker.template.TemplateModel;
import freemarker.template.TemplateModelException;
import freemarker.template.Version;

import java.util.LinkedHashMap;

public class IronTestObjectWrapper extends DefaultObjectWrapper {
    public IronTestObjectWrapper(Version incompatibleImprovements) {
        super(incompatibleImprovements);
    }

    @Override
    public TemplateModel wrap(Object obj) throws TemplateModelException {
        if (obj instanceof LinkedHashMap) {
            LinkedHashMap map = (LinkedHashMap) obj;
            if (map.containsKey("serializeFieldValueToJSONInFreeMarkerTemplate")) {
                return new LinkedHashMapJSONAdapter(map, this);
            }
        }

        return super.wrap(obj);
    }
}
