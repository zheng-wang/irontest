<div class="row">
  <div class="col-lg-12"><b>${stepRun.teststep.name}</b></div>
</div>

<div class="row">
  <div class="col-lg-1">Result:</div>
  <div class="col-lg-1 test-result-color-${stepRun.result}">${stepRun.result}</div>
  <div class="col-lg-1">Start Time:</div>
  <div class="col-lg-2">${ stepRun.startTime?datetime }</div>
  <div class="col-lg-1">Duration:</div>
  <div class="col-lg-2">${ stepRun.duration } ms</div>
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

<#if stepRun.teststep.request??>
  <div class="row">
    <div class="col-lg-1">Request:</div>
    <div class="col-lg-11">
      <textarea class="form-control" rows="8" readonly>${ stepRun.teststep.request }</textarea>
    </div>
  </div>
</#if>

<#-- Extra request info for MQ step Enqueue action with RFH2 header -->
<#if stepRun.teststep.type == "MQ" && stepRun.teststep.action == "Enqueue" &&
    stepRun.teststep.otherProperties.enqueueMessageRFH2Header.enabled == true>
  <div class="row">
    <div class="col-lg-2">RFH2 Header Folders:</div>
    <div class="col-lg-10">
      <#list stepRun.teststep.otherProperties.enqueueMessageRFH2Header.folders as rfh2Folder>
        <div class="row">
          <div class="col-lg-12"><#escape x as x?html>${ rfh2Folder.string }</#escape></div>
        </div>
      </#list>
    </div>
  </div>
</#if>

<#if stepRun.response?? &&
    (stepRun.teststep.type != "MQ" || (stepRun.teststep.type == "MQ" && stepRun.response.value??))>
  <div class="row">
    <div class="col-lg-1">Response: </div>
    <div class="col-lg-11"><#include "${stepRun.teststep.type?lower_case}TeststepResponse.ftl"></div>
  </div>
</#if>

<#if stepRun.errorMessage??>
  <div class="row">
    <div class="col-lg-1">Error:</div>
    <div class="col-lg-11">${stepRun.errorMessage}</div>
  </div>
</#if>

<#list stepRun.assertionVerifications as verification>
  <div class="row">
    <div class="col-lg-1">Assertion:</div>
    <div class="col-lg-11">
      <div class="row">
        <div class="col-lg-2">Verification result:</div>
        <div class="col-lg-1 test-result-color-${verification.verificationResult.result}">
            ${verification.verificationResult.result}
        </div>
      </div>
      <div class="row">
        <#if verification.verificationResult.error??>
          <div class="col-lg-2">Error:</div>
          <div class="col-lg-10">${verification.verificationResult.error}</div>
        <#else>
          <div class="col-lg-1">Expected:</div>
          <div class="col-lg-5">
            <#include "../assertion/${verification.assertion.type?lower_case}AssertionExpected.ftl">
          </div>
          <div class="col-lg-1">Actual:</div>
          <div class="col-lg-5">
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
