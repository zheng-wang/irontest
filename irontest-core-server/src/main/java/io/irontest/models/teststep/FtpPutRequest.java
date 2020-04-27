package io.irontest.models.teststep;

public class FtpPutRequest extends APIRequest {
    private FtpPutFileFrom fileFrom = FtpPutFileFrom.TEXT;
    private String remoteFilePath;

    public FtpPutFileFrom getFileFrom() {
        return fileFrom;
    }

    public void setFileFrom(FtpPutFileFrom fileFrom) {
        this.fileFrom = fileFrom;
    }

    public String getRemoteFilePath() {
        return remoteFilePath;
    }

    public void setRemoteFilePath(String remoteFilePath) {
        this.remoteFilePath = remoteFilePath;
    }
}
