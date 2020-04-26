package io.irontest.models.teststep;

public class FtpPutRequestFileFromText extends FtpPutRequest {
    private String fileContent;
    private String targetFileName;

    public String getFileContent() {
        return fileContent;
    }

    public void setFileContent(String fileContent) {
        this.fileContent = fileContent;
    }

    public String getTargetFileName() {
        return targetFileName;
    }

    public void setTargetFileName(String targetFileName) {
        this.targetFileName = targetFileName;
    }
}
