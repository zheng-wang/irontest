<div class="form-group"></div> <#-- spacer -->

<#if stepRun.response??>
  <div class="row">
    <div class="col-lg-12">
      <#t><#include "${stepRun.teststep.type?lower_case}TeststepResponse.ftl">
    </div>
  </div>
</#if>