package io.irontest.models.mixin;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonView;
import com.github.tomakehurst.wiremock.http.RequestMethod;
import io.irontest.resources.ResourceJsonViews;

/**
 * MixIn for {@link com.github.tomakehurst.wiremock.matching.RequestPattern}.
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonView(ResourceJsonViews.TestcaseExport.class)
public class RequestPatternMixIn {
    @JsonView({ResourceJsonViews.HTTPStubUIGrid.class, ResourceJsonViews.MockServerStubInstanceList.class, ResourceJsonViews.TestcaseExport.class})
    String url;

    //  there is no such field 'urlPattern' in the com.github.tomakehurst.wiremock.matching.RequestPattern class, so have to use the method
    @JsonView({ResourceJsonViews.HTTPStubUIGrid.class, ResourceJsonViews.MockServerStubInstanceList.class, ResourceJsonViews.TestcaseExport.class})
    String getUrlPattern() { return null; }

    @JsonView({ResourceJsonViews.HTTPStubUIGrid.class, ResourceJsonViews.TestcaseExport.class})
    RequestMethod method;
}
