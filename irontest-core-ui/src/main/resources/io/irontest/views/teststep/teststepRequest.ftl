<div class="form-group"></div> <#-- spacer -->

<#if teststep.type == 'HTTP' || teststep.type == 'SOAP'>
  <#include "httpTeststepRequest.ftl">
<#elseif teststep.type == "JMS">
  <#include "jmsTeststepRequest.ftl">
<#elseif teststep.type == "FTP">
  <#include "ftpTeststepRequest.ftl">
<#elseif teststep.type == "MQ">
  <#include "mqTeststepRequest.ftl">
<#elseif teststep.type == "MQTT">
  <#include "mqttTeststepRequest.ftl">
<#else>
  <div class="row">
    <div class="col-lg-12">
      <textarea class="form-control message-body-textarea" readonly>${ ironTestUtilsAdatper.prettyPrintJSONOrXML(teststep.request) }</textarea>
    </div>
  </div>
</#if>