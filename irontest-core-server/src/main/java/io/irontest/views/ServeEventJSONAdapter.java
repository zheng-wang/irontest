package io.irontest.views;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.tomakehurst.wiremock.stubbing.ServeEvent;
import freemarker.template.TemplateModel;
import freemarker.template.TemplateModelException;
import freemarker.template.TemplateScalarModel;
import freemarker.template.WrappingTemplateModel;
import io.irontest.utils.IronTestUtils;

import javax.xml.transform.TransformerException;
import java.io.IOException;

/**
 * Convert {@link ServeEvent} object to JSON string.
 */
public class ServeEventJSONAdapter extends WrappingTemplateModel implements TemplateModel, TemplateScalarModel {
    private ServeEvent serveEvent;

    public ServeEventJSONAdapter(ServeEvent serveEvent, IronTestObjectWrapper ow) {
        super(ow);
        this.serveEvent = serveEvent;
    }

    @Override
    public String getAsString() throws TemplateModelException {
        ObjectMapper objectMapper = new ObjectMapper();
        IronTestUtils.addMixInsForWireMock(objectMapper);
        String json;
        try {
            json = IronTestUtils.prettyPrintJSONOrXML(objectMapper.writeValueAsString(serveEvent));
        } catch (TransformerException | IOException e) {
            throw new TemplateModelException(e);
        }
        return json;
    }
}
