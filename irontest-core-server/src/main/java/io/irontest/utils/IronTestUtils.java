package io.irontest.utils;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.tomakehurst.wiremock.WireMockServer;
import com.github.tomakehurst.wiremock.common.Encoding;
import com.github.tomakehurst.wiremock.http.LoggedResponse;
import com.github.tomakehurst.wiremock.http.ResponseDefinition;
import com.github.tomakehurst.wiremock.matching.*;
import com.github.tomakehurst.wiremock.stubbing.ServeEvent;
import com.github.tomakehurst.wiremock.stubbing.StubMapping;
import com.github.tomakehurst.wiremock.verification.LoggedRequest;
import com.github.tomakehurst.wiremock.verification.notmatched.PlainTextStubNotMatchedRenderer;
import com.google.common.net.UrlEscapers;
import io.irontest.core.teststep.HTTPAPIResponse;
import io.irontest.db.SQLStatementType;
import io.irontest.models.*;
import io.irontest.models.mixin.*;
import io.irontest.models.teststep.HTTPHeader;
import io.irontest.models.teststep.MQRFH2Folder;
import org.antlr.runtime.ANTLRStringStream;
import org.apache.commons.codec.binary.Base64;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.validator.routines.UrlValidator;
import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.HttpHeaders;
import org.apache.http.HttpHost;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.ResponseHandler;
import org.apache.http.client.methods.*;
import org.apache.http.conn.ssl.NoopHostnameVerifier;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.conn.ssl.TrustStrategy;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.ssl.SSLContextBuilder;
import org.apache.http.util.EntityUtils;
import org.apache.maven.artifact.versioning.DefaultArtifactVersion;
import org.jdbi.v3.core.Jdbi;
import org.jdbi.v3.core.internal.SqlScriptParser;
import org.w3c.dom.Document;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.SSLContext;
import javax.xml.transform.TransformerException;
import javax.xml.xpath.XPathExpressionException;
import java.io.IOException;
import java.net.*;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.*;
import java.util.concurrent.atomic.AtomicReference;

import static com.github.tomakehurst.wiremock.common.Metadata.metadata;
import static io.irontest.IronTestConstants.WIREMOCK_STUB_METADATA_ATTR_NAME_IRON_TEST_ID;
import static io.irontest.IronTestConstants.WIREMOCK_STUB_METADATA_ATTR_NAME_IRON_TEST_NUMBER;

public final class IronTestUtils {
    /**
     * @param rs
     * @return a list of lower case column names present in the result set.
     * @throws SQLException
     */
    public static List<String> getFieldsPresentInResultSet(ResultSet rs) throws SQLException {
        List<String> fieldsPresentInResultSet = new ArrayList<String>();
        ResultSetMetaData metaData = rs.getMetaData();
        for(int index =1; index <= metaData.getColumnCount(); index++) {
            fieldsPresentInResultSet.add(metaData.getColumnLabel(index).toLowerCase());
        }
        return fieldsPresentInResultSet;
    }

    public static boolean isSQLRequestSingleSelectStatement(String sqlRequest) {
        List<String> statements = getStatements(sqlRequest);
        return statements.size() == 1 && SQLStatementType.isSelectStatement(statements.get(0));
    }

    /**
     * Parse the sqlRequest to get SQL statements, trimmed and without comments.
     * @param sqlRequest
     * @return
     */
    public static List<String> getStatements(String sqlRequest) {
        final List<String> statements = new ArrayList<>();
        String lastStatement = new SqlScriptParser((t, sb) -> {
            statements.add(sb.toString().trim());
            sb.setLength(0);
        }).parse(new ANTLRStringStream(sqlRequest));
        statements.add(lastStatement.trim());
        statements.removeAll(Collections.singleton(""));   //  remove all empty statements

        return statements;
    }

    public static Map<String, String> udpListToMap(List<UserDefinedProperty> testcaseUDPs) {
        Map<String, String> result = new HashMap<>();
        for (UserDefinedProperty udp: testcaseUDPs) {
            result.put(udp.getName(), udp.getValue());
        }
        return result;
    }

    public static void checkDuplicatePropertyNameBetweenDataTableAndUPDs(Set<String> udpNames, DataTable dataTable) {
        Set<String> set = new HashSet<>();
        set.addAll(udpNames);
        for (DataTableColumn dataTableColumn : dataTable.getColumns()) {
            if (!set.add(dataTableColumn.getName())) {
                throw new RuntimeException("Duplicate property name between data table and UDPs: " + dataTableColumn.getName());
            }
        }
    }

    /**
     * This method trusts all SSL certificates exposed by the API.
     *
     * @param url
     * @param username
     * @param password
     * @param httpMethod
     * @param httpHeaders
     * @param httpBody
     * @return
     * @throws Exception
     */
    public static HTTPAPIResponse invokeHTTPAPI(String url, String username, String password, HTTPMethod httpMethod,
                                                List<HTTPHeader> httpHeaders, String httpBody) throws Exception {
        UrlValidator urlValidator = new UrlValidator(new String[] {"http", "https"}, UrlValidator.ALLOW_LOCAL_URLS);
        if (!urlValidator.isValid(url)) {
            throw new RuntimeException("Invalid URL");
        }

        //  to allow special characters like whitespace in query parameters
        String safeUrl = UrlEscapers.urlFragmentEscaper().escape(url);

        //  create HTTP request object and set body if applicable
        HttpUriRequest httpRequest;
        switch (httpMethod) {
            case GET:
                httpRequest = new HttpGet(safeUrl);
                break;
            case POST:
                HttpPost httpPost = new HttpPost(safeUrl);
                httpPost.setEntity(httpBody == null ? null : new StringEntity(httpBody, "UTF-8"));    //  StringEntity doesn't accept null string (exception is thrown)
                httpRequest = httpPost;
                break;
            case PUT:
                HttpPut httpPut = new HttpPut(safeUrl);
                httpPut.setEntity(httpBody == null ? null : new StringEntity(httpBody, "UTF-8"));     //  StringEntity doesn't accept null string (exception is thrown)
                httpRequest = httpPut;
                break;
            case DELETE:
                httpRequest = new HttpDelete(safeUrl);
                break;
            default:
                throw new IllegalArgumentException("Unrecognized HTTP method " + httpMethod);
        }

        //  set request HTTP headers
        for (HTTPHeader httpHeader : httpHeaders) {
            httpRequest.setHeader(httpHeader.getName(), httpHeader.getValue());
        }
        //  set HTTP basic auth
        if (!"".equals(StringUtils.trimToEmpty(username))) {
            String auth = username + ":" + password;
            String encodedAuth = Base64.encodeBase64String(auth.getBytes());
            String authHeader = "Basic " + encodedAuth;
            httpRequest.setHeader(HttpHeaders.AUTHORIZATION, authHeader);
        }

        final HTTPAPIResponse apiResponse = new HTTPAPIResponse();
        final AtomicReference<Date> responseReceivedTime = new AtomicReference<>();
        ResponseHandler<Void> responseHandler = httpResponse -> {
            responseReceivedTime.set(new Date());
            apiResponse.setStatusCode(httpResponse.getStatusLine().getStatusCode());
            apiResponse.getHttpHeaders().add(
                    new HTTPHeader("*Status-Line*", httpResponse.getStatusLine().toString()));
            Header[] headers = httpResponse.getAllHeaders();
            for (Header header: headers) {
                apiResponse.getHttpHeaders().add(new HTTPHeader(header.getName(), header.getValue()));
            }
            HttpEntity entity = httpResponse.getEntity();
            apiResponse.setHttpBody(entity != null ? EntityUtils.toString(entity) : null);
            return null;
        };

        //  build HTTP Client instance, trusting all SSL certificates, using system HTTP proxy if needed and exists
        SSLContext sslContext = new SSLContextBuilder().loadTrustMaterial((TrustStrategy) (chain, authType) -> true).build();
        HostnameVerifier allowAllHosts = new NoopHostnameVerifier();
        SSLConnectionSocketFactory connectionFactory = new SSLConnectionSocketFactory(sslContext, allowAllHosts);
        HttpClientBuilder httpClientBuilder = HttpClients.custom().setSSLSocketFactory(connectionFactory);
        InetAddress urlHost = InetAddress.getByName(new URL(url).getHost());
        if (!(urlHost.isLoopbackAddress() || urlHost.isSiteLocalAddress())) {    //  only use system proxy for external address
            Proxy systemHTTPProxy = getSystemHTTPProxy();
            if (systemHTTPProxy != null) {
                InetSocketAddress addr = (InetSocketAddress) systemHTTPProxy.address();
                httpClientBuilder.setProxy(new HttpHost(addr.getHostName(), addr.getPort()));
            }
        }
        HttpClient httpClient = httpClientBuilder.build();

        //  invoke the API
        Date invocationStartTime = new Date();
        try {
            httpClient.execute(httpRequest, responseHandler);
        } catch (ClientProtocolException e) {
            throw new RuntimeException(e.getCause().getMessage(), e);
        }

        long responseTime = responseReceivedTime.get().getTime() - invocationStartTime.getTime();
        apiResponse.setResponseTime(responseTime);

        return apiResponse;
    }

    public static Proxy getSystemHTTPProxy() {
        List<Proxy> proxyList;
        try {
            proxyList = ProxySelector.getDefault().select(new URI("http://foo/bar"));
        } catch (URISyntaxException e) {
            throw new RuntimeException("Failed to get system proxy", e);
        }

        for (Proxy proxy: proxyList) {
            if (proxy.type() == Proxy.Type.HTTP) {
                return proxy;
            }
        }
        return null;
    }

    /**
     * Check whether the input string is potentially json or xml, and return pretty printed string accordingly.
     * If the input string is not a well formed json or xml, return it as is.
     * If the input is null, return null.
     * @param input
     * @return
     * @throws TransformerException
     */
    public static String prettyPrintJSONOrXML(String input) throws TransformerException, IOException, XPathExpressionException {
        if (input == null) {
            return null;
        }

        String trimmedInput = input.trim();
        if (trimmedInput.toUpperCase().startsWith("<!DOCTYPE HTML")) {
            //  not formatting html with DOCTYPE for now, as
            //    1. it could cause https://stackoverflow.com/questions/39189174/dom-parser-freezes-with-an-html-having-a-doctype-declaration
            //    2. if using DocumentBuilderFactory.setAttribute("http://apache.org/xml/features/nonvalidating/load-external-dtd", false),
            //        the freeze can be avoided, but the returned pretty printed html from XMLUtils.prettyPrintXML method is missing the DOCTYPE declaration.
            //        This is possibly due to https://stackoverflow.com/questions/6637076/parsing-xml-with-dom-doctype-gets-erased
            //  an example declaration: <!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Strict//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-strict.dtd">
            return input;
        } else if (trimmedInput.startsWith("<") && trimmedInput.endsWith(">")) {     //  potentially xml (impossible to be json)
            return XMLUtils.prettyPrintXML(input);
        } else if (trimmedInput.startsWith("[") || trimmedInput.startsWith("{")) {   //  potentially json array/object (impossible to be xml)
            //  notice that string "111 222 333" will be parsed by Jackson as Integer 111, so only pretty print potential json array/object here.
            ObjectMapper objectMapper = new ObjectMapper();
            objectMapper.enable(JsonParser.Feature.STRICT_DUPLICATE_DETECTION);  //  haven't found a way, without custom code, to pretty print JSON with duplicate keys (which is invalid JSON)
            Object jsonObject;
            try {
                jsonObject = objectMapper.readValue(input, Object.class);
            } catch (Exception e) {
                //  the input string is not well formed JSON
                return input;
            }
            return objectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(jsonObject);
        } else {
            return input;
        }
    }

    public static void addMixInsForWireMock(ObjectMapper objectMapper) {
        objectMapper.addMixIn(StubMapping.class, StubMappingMixIn.class);
        objectMapper.addMixIn(RequestPattern.class, RequestPatternMixIn.class);
        objectMapper.addMixIn(StringValuePattern.class, StringValuePatternMixIn.class);
        objectMapper.addMixIn(ResponseDefinition.class, ResponseDefinitionMixIn.class);
        objectMapper.addMixIn(ContentPattern.class, ContentPatternMixIn.class);
        objectMapper.addMixIn(LoggedResponse.class, LoggedResponseMixIn.class);
        objectMapper.addMixIn(ServeEvent.class, ServeEventMixIn.class);
        objectMapper.addMixIn(LoggedRequest.class, LoggedRequestMixIn.class);
    }

    /**
     * Create (clone) a new instance out of the stub spec, with UUID generated for the instance.
     * The instance also has the ironTestId as metadata.
     * The spec is not changed.
     * @param spec
     * @return
     */
    public static StubMapping createStubInstance(long ironTestId, short ironTestNumber, StubMapping spec) {
        StubMapping stubInstance = StubMapping.buildFrom(StubMapping.buildJsonStringFor(spec));
        stubInstance.setMetadata(metadata()
                .attr(WIREMOCK_STUB_METADATA_ATTR_NAME_IRON_TEST_ID, ironTestId)
                .attr(WIREMOCK_STUB_METADATA_ATTR_NAME_IRON_TEST_NUMBER, ironTestNumber)
                .build());
        stubInstance.setDirty(false);
        return stubInstance;
    }

    /**
     * By default, unmatched WireMock stub request (ServeEvent) does not have the actual response headers or response body.
     * This method update the unmatched serveEvent obtained from the WireMockServer by changing its response headers and body to the actual values.
     * @param serveEvent
     * @return the input serveEvent if it was matched;
     *         a new ServeEvent object with all fields same as the input serveEvent, except for the response headers and body, if it was unmatched.
     */
    public static ServeEvent updateUnmatchedStubRequest(ServeEvent serveEvent, WireMockServer wireMockServer) {
        if (serveEvent.getWasMatched()) {
            return serveEvent;
        } else {
            PlainTextStubNotMatchedRenderer renderer = (PlainTextStubNotMatchedRenderer) wireMockServer.getOptions()
                    .getNotMatchedRenderer();
            ResponseDefinition responseDefinition = renderer.render(wireMockServer, serveEvent.getRequest());
            LoggedResponse response = serveEvent.getResponse();
            com.github.tomakehurst.wiremock.http.HttpHeaders updatedHeaders = responseDefinition.getHeaders();
            String updatedBody = responseDefinition.getBody().substring(2);  //  remove the leading \r\n
            LoggedResponse updatedResponse = new LoggedResponse(response.getStatus(), updatedHeaders,
                    Encoding.encodeBase64(updatedBody.getBytes()), response.getFault(), null);
            ServeEvent updatedServeEvent = new ServeEvent(serveEvent.getId(), serveEvent.getRequest(),
                    serveEvent.getStubMapping(), serveEvent.getResponseDefinition(), updatedResponse,
                    serveEvent.getWasMatched(), serveEvent.getTiming());
            return updatedServeEvent;
        }
    }

    public static void substituteRequestBodyMainPatternValue(List<HTTPStubMapping> httpStubMappings) {
        for (HTTPStubMapping httpStubMapping: httpStubMappings) {
            StubMapping spec = httpStubMapping.getSpec();
            List<ContentPattern<?>> requestBodyPatterns = spec.getRequest().getBodyPatterns();
            if (requestBodyPatterns != null) {
                for (int i = 0; i < requestBodyPatterns.size(); i++) {
                    ContentPattern requestBodyPattern = requestBodyPatterns.get(i);
                    if (requestBodyPattern instanceof EqualToXmlPattern) {
                        EqualToXmlPattern equalToXmlPattern = (EqualToXmlPattern) requestBodyPattern;
                        requestBodyPatterns.set(i, new EqualToXmlPattern(
                                httpStubMapping.getRequestBodyMainPatternValue(),
                                equalToXmlPattern.isEnablePlaceholders(),
                                equalToXmlPattern.getPlaceholderOpeningDelimiterRegex(),
                                equalToXmlPattern.getPlaceholderClosingDelimiterRegex()));
                        break;
                    } else if (requestBodyPattern instanceof EqualToJsonPattern) {
                        EqualToJsonPattern equalToJsonPattern = (EqualToJsonPattern) requestBodyPattern;
                        requestBodyPatterns.set(i, new EqualToJsonPattern(
                                httpStubMapping.getRequestBodyMainPatternValue(),
                                equalToJsonPattern.isIgnoreArrayOrder(), equalToJsonPattern.isIgnoreExtraElements()));
                        break;
                    }
                }
            }
        }
    }

    public static void validateMQRFH2FolderStringAndSetFolderName(MQRFH2Folder folder) {
        //  validate folder string is well formed XML
        Document doc;
        try {
            doc = XMLUtils.xmlStringToDOM(folder.getString());
        } catch (Exception e) {
            throw new RuntimeException("Folder string is not a valid XML. " + folder.getString(), e);
        }

        //  update folder name to be the XML root element name
        folder.setName(doc.getDocumentElement().getTagName());
    }

    public static String base64EncodeByteArray(byte[] bytes) {
        return bytes == null ? null : Base64.encodeBase64String(bytes);
    }
}
