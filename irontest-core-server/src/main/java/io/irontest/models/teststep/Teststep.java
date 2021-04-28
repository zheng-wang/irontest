package io.irontest.models.teststep;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.annotation.JsonView;
import io.irontest.models.Properties;
import io.irontest.models.assertion.Assertion;
import io.irontest.models.endpoint.Endpoint;
import io.irontest.models.propertyextractor.PropertyExtractor;
import io.irontest.resources.ResourceJsonViews;

import java.util.ArrayList;
import java.util.List;

public class Teststep {
    public static final String TYPE_HTTP = "HTTP";
    public static final String TYPE_SOAP = "SOAP";
    public static final String TYPE_DB = "DB";
    public static final String TYPE_JMS = "JMS";
    public static final String TYPE_FTP = "FTP";
    public static final String TYPE_MQ = "MQ";
    public static final String TYPE_IIB = "IIB";
    public static final String TYPE_AMQP = "AMQP";
    public static final String TYPE_MQTT = "MQTT";
    public static final String TYPE_WAIT = "Wait";

    public static final String TYPE_WAIT_UNTIL_NEXT_SECOND = "WaitUntilNextSecond";
    public static final String TYPE_HTTP_STUBS_SETUP = "HTTPStubsSetup";
    public static final String TYPE_HTTP_STUB_REQUESTS_CHECK = "HTTPStubRequestsCheck";

    /* of IIB test step */
    public static final String ACTION_START = "Start";
    public static final String ACTION_STOP = "Stop";
    public static final String ACTION_WAIT_FOR_PROCESSING_COMPLETION = "WaitForProcessingCompletion";

    /* of MQ test step */
    public static final String ACTION_ENQUEUE = "Enqueue";
    public static final String ACTION_DEQUEUE = "Dequeue";

    /* of JMS test step */
    public static final String ACTION_SEND = "Send";
    public static final String ACTION_BROWSE = "Browse";

    /* of both MQ test step and JMS test step */
    public static final String ACTION_CLEAR = "Clear";
    public static final String ACTION_CHECK_DEPTH = "CheckDepth";
    public static final String ACTION_PUBLISH = "Publish";

    @JsonView(ResourceJsonViews.TeststepEdit.class)
    private long id;   //  id being 0 means this is dynamically created test step object (no record in the Teststep database table).
    @JsonView(ResourceJsonViews.TeststepEdit.class)
    private long testcaseId;
    private short sequence;
    @JsonView({ResourceJsonViews.TeststepEdit.class, ResourceJsonViews.TestcaseRunResultOnTestcaseEditView.class,
            ResourceJsonViews.TestcaseExport.class})
    private String name;
    @JsonView({ResourceJsonViews.TeststepEdit.class, ResourceJsonViews.TestcaseExport.class})
    private String type;
    @JsonView({ResourceJsonViews.TeststepEdit.class, ResourceJsonViews.TestcaseExport.class})
    private String description;
    @JsonView({ResourceJsonViews.TeststepEdit.class, ResourceJsonViews.TestcaseExport.class})
    private String action;            //  currently only used in MQ test step and IIB test step
    @JsonView({ResourceJsonViews.TeststepEdit.class, ResourceJsonViews.TestcaseExport.class})
    private Endpoint endpoint;
    @JsonView(ResourceJsonViews.TestcaseExport.class)
    private String endpointProperty;
    @JsonView({ResourceJsonViews.TeststepEdit.class, ResourceJsonViews.TestcaseExport.class})
    private Object request;
    @JsonView({ResourceJsonViews.TeststepEdit.class, ResourceJsonViews.TestcaseExport.class})
    private TeststepRequestType requestType = TeststepRequestType.TEXT;
    @JsonView({ResourceJsonViews.TeststepEdit.class, ResourceJsonViews.TestcaseExport.class})
    private String requestFilename;
    @JsonView({ResourceJsonViews.TeststepEdit.class, ResourceJsonViews.TestcaseExport.class})
    private APIRequest apiRequest;
    @JsonView({ResourceJsonViews.TeststepEdit.class, ResourceJsonViews.TestcaseExport.class})
    private List<Assertion> assertions = new ArrayList<>();
    @JsonView(ResourceJsonViews.TestcaseExport.class)
    private List<PropertyExtractor> propertyExtractors = new ArrayList<>();
    @JsonView({ResourceJsonViews.TeststepEdit.class, ResourceJsonViews.TestcaseExport.class})
    @JsonTypeInfo(use = JsonTypeInfo.Id.NAME, include = JsonTypeInfo.As.EXTERNAL_PROPERTY,
            property = "type", visible = true, defaultImpl = Properties.class)
    @JsonSubTypes({
            @JsonSubTypes.Type(value = HTTPTeststepProperties.class, name = Teststep.TYPE_HTTP),
            @JsonSubTypes.Type(value = SOAPTeststepProperties.class, name = Teststep.TYPE_SOAP),
            @JsonSubTypes.Type(value = JMSTeststepProperties.class, name = Teststep.TYPE_JMS),
            @JsonSubTypes.Type(value = MQTeststepProperties.class, name = Teststep.TYPE_MQ),
            @JsonSubTypes.Type(value = IIBTeststepProperties.class, name = Teststep.TYPE_IIB),
            @JsonSubTypes.Type(value = AMQPTeststepProperties.class, name = Teststep.TYPE_AMQP),
            @JsonSubTypes.Type(value = MQTTTeststepProperties.class, name = Teststep.TYPE_MQTT),
            @JsonSubTypes.Type(value = WaitTeststepProperties.class, name = Teststep.TYPE_WAIT),
            @JsonSubTypes.Type(value = HTTPStubsSetupTeststepProperties.class, name = Teststep.TYPE_HTTP_STUBS_SETUP)})
    private Properties otherProperties = new Properties();

    public Teststep() {}

    public Teststep(String type) {
        this.type = type;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public long getTestcaseId() {
        return testcaseId;
    }

    public void setTestcaseId(long testcaseId) {
        this.testcaseId = testcaseId;
    }

    public short getSequence() {
        return sequence;
    }

    public void setSequence(short sequence) {
        this.sequence = sequence;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Object getRequest() {
        return request;
    }

    public void setRequest(Object request) {
        this.request = request;
    }

    public TeststepRequestType getRequestType() {
        return requestType;
    }

    public void setRequestType(TeststepRequestType requestType) {
        this.requestType = requestType;
    }

    public String getRequestFilename() {
        return requestFilename;
    }

    public void setRequestFilename(String requestFilename) {
        this.requestFilename = requestFilename;
    }

    public APIRequest getApiRequest() {
        return apiRequest;
    }

    public void setApiRequest(APIRequest apiRequest) {
        this.apiRequest = apiRequest;
    }

    public Endpoint getEndpoint() {
        return endpoint;
    }

    public void setEndpoint(Endpoint endpoint) {
        this.endpoint = endpoint;
    }

    public String getEndpointProperty() {
        return endpointProperty;
    }

    public void setEndpointProperty(String endpointProperty) {
        this.endpointProperty = endpointProperty;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public Properties getOtherProperties() {
        return otherProperties;
    }

    public void setOtherProperties(Properties otherProperties) {
        this.otherProperties = otherProperties;
    }

    public List<Assertion> getAssertions() {
        return assertions;
    }

    public void setAssertions(List<Assertion> assertions) {
        this.assertions = assertions;
    }

    public String getAction() {
        return action;
    }

    public void setAction(String action) {
        this.action = action;
    }

    public List<PropertyExtractor> getPropertyExtractors() {
        return propertyExtractors;
    }

    public void setPropertyExtractors(List<PropertyExtractor> propertyExtractors) {
        this.propertyExtractors = propertyExtractors;
    }
}
