package io.irontest.core.assertion;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.networknt.schema.*;
import io.irontest.models.TestResult;
import io.irontest.models.assertion.AssertionVerificationResult;
import io.irontest.models.assertion.JSONValidAgainstJSONSchemaAssertionProperties;
import io.irontest.models.assertion.JSONValidAgainstJSONSchemaAssertionVerificationResult;
import org.apache.commons.lang3.StringUtils;

import java.util.Set;
import java.util.StringJoiner;

public class JSONValidAgainstJSONSchemaAssertionVerifier extends AssertionVerifier {
    /**
     *
     * @param inputs  contains only one argument: the JSON string that the assertion is verifying against the JSON Schema
     * @return
     * @throws Exception
     */
    @Override
    public AssertionVerificationResult verify(Object... inputs) throws Exception {
        String jsonString = (String) inputs[0];
        JSONValidAgainstJSONSchemaAssertionProperties assertionProperties =
                (JSONValidAgainstJSONSchemaAssertionProperties) getAssertion().getOtherProperties();
        String fileName = StringUtils.trimToEmpty(assertionProperties.getFileName());

        //  validate arguments
        if (jsonString == null) {
            throw new IllegalArgumentException("JSON is null.");
        } else if ("".equals(fileName)) {
            throw new IllegalArgumentException("JSON schema file not uploaded.");
        } else if (!fileName.toLowerCase().endsWith(".json")) {
            throw new IllegalArgumentException("Unrecognized JSON file format.");
        }

        JSONValidAgainstJSONSchemaAssertionVerificationResult result = new JSONValidAgainstJSONSchemaAssertionVerificationResult();
        String schemaString = new String(assertionProperties.getFileBytes());
        ObjectMapper objectMapper = new ObjectMapper();
        JsonNode jsonNode = objectMapper.readTree(jsonString);
        JsonNode schemaNode = objectMapper.readTree(schemaString);
        SpecVersion.VersionFlag flag = SpecVersionDetector.detect(schemaNode);
        JsonSchemaFactory schemaFactory = JsonSchemaFactory.getInstance(flag);
        JsonSchema schema = schemaFactory.getSchema(schemaNode);
        Set<ValidationMessage> validationResult = schema.validate(jsonNode);

        if (validationResult.isEmpty()) {
            result.setResult(TestResult.PASSED);
        } else {
            result.setResult(TestResult.FAILED);
            StringJoiner failureDetails = new StringJoiner("\n");
            validationResult.forEach(validationMessage -> failureDetails.add(validationMessage.toString() + "."));
            result.setFailureDetails(failureDetails.toString());
        }

        return result;
    }
}
