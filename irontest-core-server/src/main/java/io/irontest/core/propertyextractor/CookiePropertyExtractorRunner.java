package io.irontest.core.propertyextractor;

import io.irontest.models.propertyextractor.CookiePropertyExtractorProperties;
import org.apache.commons.lang3.StringUtils;

import java.util.Arrays;
import java.util.Map;
import java.util.stream.Collectors;

public class CookiePropertyExtractorRunner extends PropertyExtractorRunner {
    @Override
    public String extract(String setCookieHeaderValue) {
        CookiePropertyExtractorProperties otherProperties =
                (CookiePropertyExtractorProperties) getPropertyExtractor().getOtherProperties();
        String cookieName = otherProperties.getCookieName();

        //  validate arguments
        if ("".equals(StringUtils.trimToEmpty(setCookieHeaderValue))) {
            throw new IllegalArgumentException("No Set-Cookie header in the HTTP response.");
        } else if ("".equals(StringUtils.trimToEmpty(cookieName))) {
            throw new IllegalArgumentException("Cookie name not specified.");
        }

        Map<String, String> nameValuePairCookies = Arrays.stream(setCookieHeaderValue.split(";"))
                .filter(cookie -> cookie.contains("="))
                .map(cookie -> cookie.split("="))
                .collect(Collectors.toMap(
                        pair -> pair[0].trim(),  //  key
                        pair -> pair[1].trim()   //  value
                ));

        if (!nameValuePairCookies.containsKey(cookieName)) {
            throw new RuntimeException("Cookie " + cookieName + " does not exist in the HTTP response.");
        }

        return nameValuePairCookies.get(cookieName);
    }
}
