package io.irontest.models.teststep;

public class FtpPutRequestFileFromText extends FtpPutRequest {
    private String fileContent;

    public String getFileContent() {
        return fileContent;
    }

    public void setFileContent(String fileContent) {
        this.fileContent = fileContent;
    }
}
