package io.irontest.core.runner;

import io.irontest.models.Teststep;
import org.apache.http.HttpEntity;
import org.apache.http.HttpHeaders;
import org.apache.http.HttpResponse;
import org.apache.http.client.ResponseHandler;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;

import java.io.IOException;

/**
 * Created by Trevor Li on 7/14/15.
 */
public class SOAPTeststepRunner implements TeststepRunner {
    public SOAPTeststepRunResult run(Teststep teststep) throws Exception {
        final SOAPTeststepRunResult result = new SOAPTeststepRunResult();

        CloseableHttpClient httpclient = HttpClients.createDefault();
        HttpPost httpPost = new HttpPost(teststep.getEndpoint().getUrl());
        httpPost.setEntity(new StringEntity((String) teststep.getRequest()));
        ResponseHandler<Void> responseHandler = new ResponseHandler<Void>() {
            public Void handleResponse(final HttpResponse response) throws IOException {
                String contentType = response.getFirstHeader(HttpHeaders.CONTENT_TYPE).getValue();
                result.setResponseContentType(contentType);
                HttpEntity entity = response.getEntity();
                result.setResponseBody(entity != null ? EntityUtils.toString(entity) : null);
                return null;
            }
        };
        httpclient.execute(httpPost, responseHandler);

        return result;
    }
}
