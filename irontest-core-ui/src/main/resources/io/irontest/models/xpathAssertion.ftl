<div class="col-lg-12">XPath "${ verification.assertion.otherProperties.xPath }"
  <#if verification.verificationResult.result == "Passed">evaluated<#else>did not evaluate</#if>
  to "${ verification.assertion.otherProperties.expectedValue }".
  <#if verification.verificationResult.result == "Failed">
    The actual value was "${ verification.verificationResult.actualValue }".
  </#if>
</div>
