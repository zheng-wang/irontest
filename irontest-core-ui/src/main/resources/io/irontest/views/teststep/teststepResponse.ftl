<div class="form-group"></div> <#-- spacer -->

<#if stepRun.response??>
  <#assign response = stepRun.response>
  <#if teststep.type == "HTTPStubRequestsCheck">
    <div class="row">
      <div class="col-lg-1" id="stub-requests-in-step-run-${ stepRun.id?string.computer }">Stub Requests</div>
      <div class="col-lg-11">
        <#t><#include "${teststep.type?lower_case}TeststepResponse.ftl">
      </div>
    </div>
  <#else>
    <div class="row">
      <div class="col-lg-12">
        <#t><#include "${stepRun.teststep.type?lower_case}TeststepResponse.ftl">
      </div>
    </div>
  </#if>
</#if>