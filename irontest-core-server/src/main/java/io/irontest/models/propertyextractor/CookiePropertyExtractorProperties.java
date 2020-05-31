package io.irontest.models.propertyextractor;

import io.irontest.models.Properties;

public class CookiePropertyExtractorProperties extends Properties {
    private String cookieName;

    public String getCookieName() {
        return cookieName;
    }

    public void setCookieName(String cookieName) {
        this.cookieName = cookieName;
    }
}
