package io.irontest.models.teststep;

public class FtpPutRequest extends APIRequest {
    private FtpPutFileFrom fileFrom = FtpPutFileFrom.TEXT;

    public FtpPutFileFrom getFileFrom() {
        return fileFrom;
    }

    public void setFileFrom(FtpPutFileFrom fileFrom) {
        this.fileFrom = fileFrom;
    }
}
