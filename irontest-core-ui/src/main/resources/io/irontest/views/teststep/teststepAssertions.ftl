<#list stepRun.assertionVerifications as verification>
  <div class="form-group"></div> <!-- spacer -->
  <div class="row">
    <div class="col-lg-1">Assertion:</div>
    <div class="col-lg-11">
      <div class="row">
        <div class="col-lg-1">Name:</div>
        <div class="col-lg-3">${verification.assertion.name}</div>
        <div class="col-lg-2">Verification result:</div>
        <div class="col-lg-1 test-result-color-${verification.verificationResult.result}">
            ${verification.verificationResult.result}
        </div>
      </div>
      <div class="form-group"></div> <!-- spacer -->
      <div class="row">
        <div class="col-lg-1">Expected:</div>
        <div class="col-lg-11">
          <div class="form-group">
            <#include "../assertion/${verification.assertion.type?lower_case}AssertionExpected.ftl">
          </div>
        </div>
      </div>
      <div class="row">
        <#if verification.verificationResult.error??>
          <div class="col-lg-1">Error:</div>
          <div class="col-lg-11">${verification.verificationResult.error}</div>
        <#else>
          <div class="col-lg-1">Actual:</div>
          <div class="col-lg-11">
            <#if verification.verificationResult.result == "Passed">
              As expected.
            <#else>
              <#include "../assertion/${verification.assertion.type?lower_case}AssertionActualWhenFailed.ftl">
            </#if>
          </div>
        </#if>
      </div>
    </div>
  </div>
</#list>