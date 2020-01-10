<div class="row">
  <div class="col-lg-12">HTTP Headers:</div>
</div>
<div class="form-group"></div> <#-- spacer -->
<div class="row">
  <div class="col-lg-12">
    <textarea rows="6" class="form-control" readonly>${ stepRun.teststep.otherProperties.httpHeaders?join("\n") }</textarea>
  </div>
</div>

<#if stepRun.teststep.type == 'SOAP' || (stepRun.teststep.type == 'HTTP' && stepRun.teststep.otherProperties.httpMethod != 'GET' && stepRun.teststep.otherProperties.httpMethod != 'DELETE')>
  <div class="form-group"></div> <#-- spacer -->
  <div class="row">
    <div class="col-lg-12">HTTP Body:</div>
  </div>
  <div class="form-group"></div> <#-- spacer -->
  <div class="row">
    <div class="col-lg-12">
      <textarea class="form-control message-body-textarea" readonly>${ ironTestUtilsAdatper.prettyPrintJSONOrXML(stepRun.teststep.request) }</textarea>
    </div>
  </div>
</#if>