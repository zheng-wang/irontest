<#ftl encoding='UTF-8'>
<div class="row" id="step-run-${ stepRun.id?string.computer }">
  <div class="col-lg-11"><h4>${ stepRun.teststep.name }</h4></div>
  <#if testcaseRun??>
    <div class="col-lg-1"><a href="#page-top">Top</a></div>
  </#if>
</div>

<div class="row">
  <div class="col-lg-1">Result:</div>
  <div class="col-lg-1 test-result-color-${stepRun.result}">${stepRun.result}</div>
  <div class="col-lg-1">Start Time:</div>
  <div class="col-lg-3">${ stepRun.startTime?datetime }</div>
  <div class="col-lg-1">Duration:</div>
  <div class="col-lg-1">${ stepRun.duration } ms</div>
</div>

<#if stepRun.teststep.description?? && stepRun.teststep.description?has_content>
  <div class="row">
    <div class="col-lg-1">Description:</div>
    <div class="col-lg-11">${ stepRun.teststep.description }</div>
  </div>
</#if>

<div class="row">
  <div class="col-lg-1">Action:</div>
  <div class="col-lg-11">
    <#include "${stepRun.teststep.type?lower_case}TeststepActionDescription.ftl">
  </div>
</div>

<#-- Request info -->
<#if stepRun.teststep.request?? || (stepRun.teststep.otherProperties.httpHeaders?? &&
    stepRun.teststep.otherProperties.httpHeaders?size > 0)>
  <div class="form-group"></div> <!-- spacer -->
  <div class="row">
    <div class="col-lg-1">Request:</div>
    <div class="col-lg-11">
      <#-- Extra request info for test step that has request HTTP headers -->
      <#if stepRun.teststep.otherProperties.httpHeaders?? &&
          (stepRun.teststep.otherProperties.httpHeaders?size > 0)>
        <div class="row">
          <div class="col-lg-2">HTTP Headers:</div>
          <div class="col-lg-10">
            <#list stepRun.teststep.otherProperties.httpHeaders as httpHeader>
              <div class="row">
                <div class="col-lg-2">${ httpHeader.name }:</div>
                <div class="col-lg-10">${ httpHeader.value }</div>
              </div>
            </#list>
          </div>
        </div>
      </#if>
      <#if stepRun.teststep.request??>
        <div class="row">
          <div class="col-lg-12">
            <#if stepRun.teststep.type == "MQ">
              <#t><#include "${stepRun.teststep.type?lower_case}TeststepRequest.ftl">
            <#else>
              <textarea class="form-control" rows="8" readonly>${ stepRun.teststep.request }</textarea>
            </#if>
          </div>
        </div>
      </#if>
    </div>
  </div>
</#if>

<#-- Response info -->
<#if stepRun.response??>
  <div class="form-group"></div> <!-- spacer -->
  <div class="row">
    <div class="col-lg-1" <#if stepRun.teststep.type == "HTTPStubRequestsCheck">id="stub-requests-in-step-run-${ stepRun.id?string.computer }"</#if>>
      ${ (stepRun.teststep.type == "HTTPStubRequestsCheck")?then('Stub Requests', 'Response') }:
    </div>
    <div class="col-lg-11">
      <#t><#include "${stepRun.teststep.type?lower_case}TeststepResponse.ftl">
    </div>
  </div>
</#if>

<#-- Some additional info about the step run -->
<#if stepRun.infoMessage??>
  <div class="row">
    <div class="col-lg-1">Info:</div>
    <div class="col-lg-11">${stepRun.infoMessage}</div>
  </div>
</#if>

<#-- Error info -->
<#if stepRun.errorMessage??>
  <div class="row">
    <div class="col-lg-1">Error:</div>
    <div class="col-lg-11">${stepRun.errorMessage}</div>
  </div>
</#if>

<#-- Assertion verifications -->
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
