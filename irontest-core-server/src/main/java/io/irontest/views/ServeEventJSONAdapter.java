package io.irontest.views;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.tomakehurst.wiremock.http.LoggedResponse;
import com.github.tomakehurst.wiremock.http.ResponseDefinition;
import com.github.tomakehurst.wiremock.matching.RequestPattern;
import com.github.tomakehurst.wiremock.stubbing.ServeEvent;
import com.github.tomakehurst.wiremock.stubbing.StubMapping;
import freemarker.template.TemplateModel;
import freemarker.template.TemplateModelException;
import freemarker.template.TemplateScalarModel;
import freemarker.template.WrappingTemplateModel;
import io.irontest.models.mixin.LoggedResponseMixIn;
import io.irontest.models.mixin.RequestPatternMixIn;
import io.irontest.models.mixin.ResponseDefinitionMixIn;
import io.irontest.models.mixin.StubMappingMixIn;
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
        objectMapper.addMixIn(StubMapping.class, StubMappingMixIn.class);
        objectMapper.addMixIn(RequestPattern.class, RequestPatternMixIn.class);
        objectMapper.addMixIn(ResponseDefinition.class, ResponseDefinitionMixIn.class);
        objectMapper.addMixIn(LoggedResponse.class, LoggedResponseMixIn.class);
        String json;
        try {
            json = IronTestUtils.prettyPrintJSONOrXML(objectMapper.writeValueAsString(serveEvent));
        } catch (TransformerException | IOException e) {
            throw new TemplateModelException(e);
        }
        return json;
    }
}
