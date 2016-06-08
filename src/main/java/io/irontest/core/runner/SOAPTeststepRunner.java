package io.irontest.core.runner;

import io.irontest.models.Teststep;
import io.irontest.utils.XMLUtils;
import org.apache.http.HttpEntity;
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
    public String run(Teststep teststep) throws Exception {
        String response = postRequest(teststep.getEndpoint().getUrl(), teststep.getRequest());
        return XMLUtils.prettyPrintXML(response);
    }

    private String postRequest(String soapAddress, String soapRequest) throws IOException {
        CloseableHttpClient httpclient = HttpClients.createDefault();
        HttpPost httpPost = new HttpPost(soapAddress);
        httpPost.setEntity(new StringEntity(soapRequest));
        ResponseHandler<String> responseHandler = new ResponseHandler<String>() {
            public String handleResponse(final HttpResponse response) throws IOException {
                HttpEntity entity = response.getEntity();
                return entity != null ? EntityUtils.toString(entity) : null;
            }
        };
        return httpclient.execute(httpPost, responseHandler);
    }
}
