package io.irontest.models.mixin;

import com.fasterxml.jackson.annotation.JsonInclude;

/**
 * MixIn for {@link com.github.tomakehurst.wiremock.http.LoggedResponse}.
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
public class LoggedResponseMixIn {
}
