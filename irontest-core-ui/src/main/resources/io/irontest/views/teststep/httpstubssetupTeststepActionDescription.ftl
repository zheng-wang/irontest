<div class="row">
  <div class="col-lg-12">Reset mock server, and load stubs:</div>
</div>
<#list stepRun.teststep.otherProperties.httpStubMappings as stubMapping>
  <div class="form-group"></div> <!-- spacer -->
  <div class="row">
    <div class="col-lg-1">${ stubMapping.number }</div>
    <div class="col-lg-11">
      <textarea class="form-control" rows="8" readonly>${ stubMapping.spec }</textarea>
    </div>
  </div>
</#list>