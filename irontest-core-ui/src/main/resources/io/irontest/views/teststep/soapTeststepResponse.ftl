<#include "httpResponseHeaders.ftl">

<div class="form-group"></div> <#-- spacer -->
<div class="row">
  <div class="col-lg-12">HTTP Body:</div>
</div>
<div class="form-group"></div> <#-- spacer -->
<div class="row">
  <div class="col-lg-12">
    <textarea class="form-control message-body-textarea" readonly>${ ironTestUtilsAdatper.prettyPrintJSONOrXML(stepRun.response.httpBody) }</textarea>
  </div>
</div>
