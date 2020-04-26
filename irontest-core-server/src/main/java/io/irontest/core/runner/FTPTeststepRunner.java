package io.irontest.core.runner;

import io.irontest.models.endpoint.Endpoint;
import io.irontest.models.teststep.APIRequest;
import io.irontest.models.teststep.FtpPutRequest;
import io.irontest.models.teststep.FtpPutRequestFileFromText;
import io.irontest.models.teststep.Teststep;
import org.apache.commons.lang3.StringUtils;

public class FTPTeststepRunner extends TeststepRunner {
    protected BasicTeststepRun run(Teststep teststep) throws Exception {
        BasicTeststepRun basicTeststepRun = new BasicTeststepRun();
        Endpoint endpoint = teststep.getEndpoint();

        APIRequest apiRequest = teststep.getApiRequest();
        if (apiRequest instanceof FtpPutRequest) {
            put(endpoint, (FtpPutRequest) apiRequest);
        }

        return basicTeststepRun;
    }

    private void put(Endpoint endpoint, FtpPutRequest ftpPutRequest) {
        if (ftpPutRequest instanceof FtpPutRequestFileFromText) {
            FtpPutRequestFileFromText ftpPutRequestFileFromText = (FtpPutRequestFileFromText) ftpPutRequest;
            String fileContent = ftpPutRequestFileFromText.getFileContent();
            String targetFileName = StringUtils.trimToEmpty(ftpPutRequestFileFromText.getTargetFileName());

            //  validate argument
            if ("".equals(StringUtils.trimToEmpty(fileContent))) {
                throw new IllegalArgumentException("No file content.");
            } else if ("".equals(targetFileName)) {
                throw new IllegalArgumentException("Target File Name not specified.");
            }

        }
    }
}
