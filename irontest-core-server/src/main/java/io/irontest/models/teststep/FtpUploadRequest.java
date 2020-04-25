package io.irontest.models.teststep;

public class FtpUploadRequest extends APIRequest {
    private FtpUploadFileFrom fileFrom = FtpUploadFileFrom.TEXT;

    public FtpUploadFileFrom getFileFrom() {
        return fileFrom;
    }

    public void setFileFrom(FtpUploadFileFrom fileFrom) {
        this.fileFrom = fileFrom;
    }
}
