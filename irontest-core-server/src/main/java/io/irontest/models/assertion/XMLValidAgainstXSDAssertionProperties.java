package io.irontest.models.assertion;

import io.irontest.models.Properties;

public class XMLValidAgainstXSDAssertionProperties extends Properties {
    private String filename;
    private byte[] fileBytes;

    public String getFilename() {
        return filename;
    }

    public void setFilename(String filename) {
        this.filename = filename;
    }

    public byte[] getFileBytes() {
        return fileBytes;
    }

    public void setFileBytes(byte[] fileBytes) {
        this.fileBytes = fileBytes;
    }
}
