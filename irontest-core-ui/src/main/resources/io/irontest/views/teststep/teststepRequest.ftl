<div class="form-group"></div> <#-- spacer -->

<#if stepRun.teststep.type == 'HTTP' || stepRun.teststep.type == 'SOAP'>
  <#include "httpTeststepRequest.ftl">
<#elseif stepRun.teststep.type == "MQ">
  <#include "mqTeststepRequest.ftl">
<#else>
  <div class="row">
    <div class="col-lg-12">
      <textarea class="form-control message-body-textarea" readonly>${ ironTestUtilsAdatper.prettyPrintJSONOrXML(stepRun.teststep.request) }</textarea>
    </div>
  </div>
</#if>