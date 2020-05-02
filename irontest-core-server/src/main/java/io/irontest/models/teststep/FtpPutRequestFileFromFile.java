package io.irontest.models.teststep;

public class FtpPutRequestFileFromFile extends FtpPutRequest {
    private String fileName;
    private byte[] fileContent;

    public FtpPutRequestFileFromFile() {}

    public FtpPutRequestFileFromFile(FtpPutRequest ftpPutRequest) {
        setFileFrom(ftpPutRequest.getFileFrom());
        setRemoteFilePath(ftpPutRequest.getRemoteFilePath());
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public byte[] getFileContent() {
        return fileContent;
    }

    public void setFileContent(byte[] fileContent) {
        this.fileContent = fileContent;
    }
}
