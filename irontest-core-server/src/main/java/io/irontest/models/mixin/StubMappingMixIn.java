package io.irontest.models.mixin;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonView;
import com.github.tomakehurst.wiremock.matching.RequestPattern;
import io.irontest.resources.ResourceJsonViews;

/**
 * MixIn for {@link com.github.tomakehurst.wiremock.stubbing.StubMapping}.
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonView({ResourceJsonViews.MockServerStubInstanceList.class, ResourceJsonViews.TestcaseExport.class})
public abstract class StubMappingMixIn {
    @JsonView({ResourceJsonViews.HTTPStubUIGrid.class, ResourceJsonViews.MockServerStubInstanceList.class,
            ResourceJsonViews.TestcaseExport.class})
    RequestPattern request;

    //  ignore this WireMock internal field
    @JsonIgnore
    abstract long getInsertionIndex();
}
