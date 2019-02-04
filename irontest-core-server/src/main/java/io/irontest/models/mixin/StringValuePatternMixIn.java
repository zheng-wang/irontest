package io.irontest.models.mixin;

import com.fasterxml.jackson.annotation.JsonView;
import io.irontest.resources.ResourceJsonViews;

/**
 * MixIn for {@link com.github.tomakehurst.wiremock.matching.StringValuePattern}.
 */
@JsonView(ResourceJsonViews.TestcaseExport.class)
public class StringValuePatternMixIn {
}
