package io.irontest.core.runner;

import io.irontest.models.endpoint.Endpoint;
import io.irontest.models.teststep.HTTPHeader;
import io.irontest.models.teststep.HTTPTeststepProperties;
import io.irontest.models.teststep.Teststep;
import org.apache.commons.codec.binary.Base64;
import org.apache.commons.lang3.StringUtils;
import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.HttpHeaders;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.ResponseHandler;
import org.apache.http.client.methods.*;
import org.apache.http.conn.ssl.TrustStrategy;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.ssl.SSLContextBuilder;
import org.apache.http.util.EntityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.net.ssl.SSLContext;
import java.io.IOException;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;

public class HTTPTeststepRunner extends TeststepRunner {
    private static final Logger LOGGER = LoggerFactory.getLogger(HTTPTeststepRunner.class);

    protected BasicTeststepRun run(Teststep teststep) throws Exception {
        BasicTeststepRun basicTeststepRun = new BasicTeststepRun();
        Endpoint endpoint = teststep.getEndpoint();

        //  create HTTP request object and set body if applicable
        HttpUriRequest httpRequest;
        HTTPTeststepProperties otherProperties = (HTTPTeststepProperties) teststep.getOtherProperties();
        switch (otherProperties.getHttpMethod()) {
            case GET:
                httpRequest = new HttpGet(endpoint.getUrl());
                break;
            case POST:
                HttpPost httpPost = new HttpPost(endpoint.getUrl());
                httpPost.setEntity(new StringEntity((String) teststep.getRequest(),"UTF-8"));
                httpRequest = httpPost;
                break;
            case PUT:
                HttpPut httpPut = new HttpPut(endpoint.getUrl());
                httpPut.setEntity(new StringEntity((String) teststep.getRequest(),"UTF-8"));
                httpRequest = httpPut;
                break;
            case DELETE:
                httpRequest = new HttpDelete(endpoint.getUrl());
                break;
            default:
                throw new IllegalArgumentException("Unrecognized HTTP method " + otherProperties.getHttpMethod());
        }

        //  set request HTTP headers
        for (HTTPHeader httpHeader : otherProperties.getHttpHeaders()) {
            httpRequest.setHeader(httpHeader.getName(), httpHeader.getValue());
        }
        //  set HTTP basic auth
        if (!"".equals(StringUtils.trimToEmpty(endpoint.getUsername()))) {
            String auth = endpoint.getUsername() + ":" + getDecryptedEndpointPassword();
            String encodedAuth = Base64.encodeBase64String(auth.getBytes());
            String authHeader = "Basic " + encodedAuth;
            httpRequest.setHeader(HttpHeaders.AUTHORIZATION, authHeader);
        }

        final HTTPAPIResponse apiResponse = new HTTPAPIResponse();
        ResponseHandler<Void> responseHandler = new ResponseHandler<Void>() {
            public Void handleResponse(final HttpResponse httpResponse) throws IOException {
                LOGGER.info(httpResponse.toString());
                apiResponse.getHttpHeaders().add(
                        new HTTPHeader("*Status-Line*", httpResponse.getStatusLine().toString()));
                Header[] headers = httpResponse.getAllHeaders();
                for (Header header: headers) {
                    apiResponse.getHttpHeaders().add(new HTTPHeader(header.getName(), header.getValue()));
                }
                HttpEntity entity = httpResponse.getEntity();
                apiResponse.setHttpBody(entity != null ? EntityUtils.toString(entity) : null);
                return null;
            }
        };

        //  build HTTP Client instance
        //  trust all SSL certificates
        SSLContext sslContext = new SSLContextBuilder().loadTrustMaterial(new TrustStrategy() {
            public boolean isTrusted(X509Certificate[] arg0, String arg1) throws CertificateException {
                return true;
            }
        }).build();
        HttpClient httpClient = HttpClients.custom().setSSLContext(sslContext).build();

        //  invoke the web service
        httpClient.execute(httpRequest, responseHandler);

        basicTeststepRun.setResponse(apiResponse);

        return basicTeststepRun;
    }
}
