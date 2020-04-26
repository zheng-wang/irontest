package io.irontest.models.teststep;

public class FtpPutRequest extends APIRequest {
    private FtpPutFileFrom fileFrom = FtpPutFileFrom.TEXT;
    private String targetFilePath;

    public FtpPutFileFrom getFileFrom() {
        return fileFrom;
    }

    public void setFileFrom(FtpPutFileFrom fileFrom) {
        this.fileFrom = fileFrom;
    }

    public String getTargetFilePath() {
        return targetFilePath;
    }

    public void setTargetFilePath(String targetFilePath) {
        this.targetFilePath = targetFilePath;
    }
}
