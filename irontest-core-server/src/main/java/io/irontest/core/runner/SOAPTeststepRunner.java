package io.irontest.core.runner;

import io.irontest.models.Endpoint;
import io.irontest.models.teststep.Teststep;
import org.apache.commons.lang3.StringUtils;
import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.HttpHeaders;
import org.apache.http.HttpResponse;
import org.apache.http.auth.AuthScope;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.client.CredentialsProvider;
import org.apache.http.client.ResponseHandler;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.BasicCredentialsProvider;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;

/**
 * Created by Trevor Li on 7/14/15.
 */
public class SOAPTeststepRunner extends TeststepRunner {
    private static final Logger LOGGER = LoggerFactory.getLogger(SOAPTeststepRunner.class);

    protected BasicTeststepRun run(Teststep teststep) throws Exception {
        BasicTeststepRun basicTeststepRun = new BasicTeststepRun();
        final SOAPAPIResponse apiResponse = new SOAPAPIResponse();

        Endpoint endpoint = teststep.getEndpoint();

        //  build http client instance
        HttpClientBuilder httpClientBuilder = HttpClients.custom();
        if (!"".equals(StringUtils.trimToEmpty(endpoint.getUsername()))) {
            CredentialsProvider provider = new BasicCredentialsProvider();
            UsernamePasswordCredentials credentials = new UsernamePasswordCredentials(
                    endpoint.getUsername(), endpoint.getPassword());
            provider.setCredentials(AuthScope.ANY, credentials);
            httpClientBuilder.setDefaultCredentialsProvider(provider);
        }
        CloseableHttpClient httpclient = httpClientBuilder.build();

        HttpPost httpPost = new HttpPost(endpoint.getUrl());
        /*SOAPTeststepProperties otherProperties = (SOAPTeststepProperties) teststep.getOtherProperties();
        if (otherProperties != null) {
            for (Map.Entry<String, String> httpHeader : otherProperties.getHttpHeaders().entrySet()) {
                httpPost.setHeader(httpHeader.getKey(), httpHeader.getValue());
            }
        }*/
        httpPost.setHeader(HttpHeaders.CONTENT_TYPE, "application/soap+xml; charset=utf-8");
        httpPost.setEntity(new StringEntity((String) teststep.getRequest(),"UTF-8"));
        ResponseHandler<Void> responseHandler = new ResponseHandler<Void>() {
            public Void handleResponse(final HttpResponse httpResponse) throws IOException {
                LOGGER.info(httpResponse.toString());
                Header contentType = httpResponse.getFirstHeader(HttpHeaders.CONTENT_TYPE);
                apiResponse.setHttpResponseContentType(contentType == null ? null : contentType.getValue());
                HttpEntity entity = httpResponse.getEntity();
                apiResponse.setHttpResponseBody(entity != null ? EntityUtils.toString(entity) : null);
                return null;
            }
        };
        httpclient.execute(httpPost, responseHandler);

        basicTeststepRun.setResponse(apiResponse);

        return basicTeststepRun;
    }
}
