package io.irontest.models.mixin;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.github.tomakehurst.wiremock.matching.ContentPattern;

/**
 * MixIn for {@link ContentPattern}.
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ContentPatternMixIn {
}
