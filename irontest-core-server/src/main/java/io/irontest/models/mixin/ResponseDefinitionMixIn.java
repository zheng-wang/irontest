package io.irontest.models.mixin;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.github.tomakehurst.wiremock.http.ResponseDefinition;

/**
 * MixIn for {@link ResponseDefinition}.
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ResponseDefinitionMixIn {
}
