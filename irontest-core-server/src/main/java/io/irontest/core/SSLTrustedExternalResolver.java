package io.irontest.core;

import com.predic8.xml.util.ExternalResolver;
import io.irontest.core.teststep.HTTPAPIResponse;
import io.irontest.models.HTTPMethod;
import io.irontest.utils.IronTestUtils;

import java.io.StringReader;
import java.net.URI;
import java.util.ArrayList;

public class SSLTrustedExternalResolver extends ExternalResolver {
    public StringReader resolveViaHttp(String url) throws Exception {
        URI uri = new URI(url);
        uri = uri.normalize();
        HTTPAPIResponse response = IronTestUtils.invokeHTTPAPI(uri.toString(), null, null, HTTPMethod.GET,
                new ArrayList<>(), null);
        return new StringReader(response.getHttpBody());
    }
}
