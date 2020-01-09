<div class="form-group"></div> <#-- spacer -->

<div class="row">
  <div class="col-lg-12">

    <#-- HTTP headers (if available) -->
    <#if stepRun.teststep.otherProperties.httpHeaders?? &&
        (stepRun.teststep.otherProperties.httpHeaders?size > 0)>
      <div class="row">
        <div class="col-lg-12">HTTP Headers:</div>
      </div>
      <div class="form-group"></div> <#-- spacer -->
      <div class="row">
        <div class="col-lg-12">
          <textarea rows="6" class="form-control" readonly>${ stepRun.teststep.otherProperties.httpHeaders?join("\n") }</textarea>
        </div>
      </div>
      <div class="form-group"></div> <#-- spacer -->
      <div class="row">
        <div class="col-lg-12">HTTP Body:</div>
      </div>
      <div class="form-group"></div> <#-- spacer -->
    </#if>

    <#if stepRun.teststep.request??>
      <div class="row">
        <div class="col-lg-12">
          <#if stepRun.teststep.type == "MQ">
            <#t><#include "${stepRun.teststep.type?lower_case}TeststepRequest.ftl">
          <#else>
            <textarea class="form-control message-body-textarea" readonly>${ ironTestUtilsAdatper.prettyPrintJSONOrXML(stepRun.teststep.request) }</textarea>
          </#if>
        </div>
      </div>
    </#if>

  </div>
</div>