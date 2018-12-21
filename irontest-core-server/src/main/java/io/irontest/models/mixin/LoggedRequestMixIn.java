package io.irontest.models.mixin;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonView;
import com.github.tomakehurst.wiremock.http.RequestMethod;
import com.github.tomakehurst.wiremock.matching.UrlPattern;
import io.irontest.resources.ResourceJsonViews;

import java.util.Date;

/**
 * MixIn for {@link com.github.tomakehurst.wiremock.verification.LoggedRequest}.
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
public class LoggedRequestMixIn {
    @JsonView({ResourceJsonViews.MockServerStubRequestList.class, ResourceJsonViews.MockServerUnmatchedRequestList.class})
    Date loggedDate;
}
