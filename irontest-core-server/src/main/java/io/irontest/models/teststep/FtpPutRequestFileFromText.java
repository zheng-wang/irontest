package io.irontest.models.teststep;

import com.fasterxml.jackson.annotation.JsonView;
import io.irontest.resources.ResourceJsonViews;

@JsonView({ResourceJsonViews.TeststepEdit.class, ResourceJsonViews.TestcaseExport.class})
public class FtpPutRequestFileFromText extends FtpPutRequest {
    private String fileContent;

    public FtpPutRequestFileFromText() {}

    public FtpPutRequestFileFromText(FtpPutRequest ftpPutRequest) {
        setFileFrom(ftpPutRequest.getFileFrom());
        setRemoteFilePath(ftpPutRequest.getRemoteFilePath());
    }

    public String getFileContent() {
        return fileContent;
    }

    public void setFileContent(String fileContent) {
        this.fileContent = fileContent;
    }
}
