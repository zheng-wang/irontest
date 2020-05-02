package io.irontest.models.teststep;

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
