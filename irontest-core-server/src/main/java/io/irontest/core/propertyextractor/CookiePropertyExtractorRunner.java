package io.irontest.core.propertyextractor;

import io.irontest.models.propertyextractor.CookiePropertyExtractorProperties;

import java.util.Arrays;
import java.util.Map;
import java.util.stream.Collectors;

public class CookiePropertyExtractorRunner extends PropertyExtractorRunner {
    @Override
    public String extract(String setCookieHeaderValue) {
        CookiePropertyExtractorProperties otherProperties =
                (CookiePropertyExtractorProperties) getPropertyExtractor().getOtherProperties();

        String cookieName = otherProperties.getCookieName();

        Map<String, String> nameValuePairCookies = Arrays.stream(setCookieHeaderValue.split(";"))
                .filter(cookie -> cookie.contains("="))
                .map(cookie -> cookie.split("="))
                .collect(Collectors.toMap(
                        pair -> pair[0].trim(),  //  key
                        pair -> pair[1].trim()   //  value
                ));

        return nameValuePairCookies.get(cookieName);
    }
}
