<div class="col-lg-12">Response
  <#if verification.verificationResult.result == "Passed">contained<#else>did not contain</#if>
  "${ verification.assertion.otherProperties.contains }".
</div>
