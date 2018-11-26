package io.irontest.models.mixin;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonView;
import com.github.tomakehurst.wiremock.http.RequestMethod;
import com.github.tomakehurst.wiremock.matching.UrlPattern;
import io.irontest.resources.ResourceJsonViews;

/**
 * MixIn for {@link com.github.tomakehurst.wiremock.matching.RequestPattern}.
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
public class RequestPatternMixIn {
    @JsonView(ResourceJsonViews.HTTPStubUIGrid.class)
    private UrlPattern url;
    @JsonView(ResourceJsonViews.HTTPStubUIGrid.class)
    private RequestMethod method;
}
