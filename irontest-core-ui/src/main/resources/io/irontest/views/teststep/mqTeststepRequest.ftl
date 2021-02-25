<div class="row">
  <div class="col-lg-1">
    Body:
  </div>
  <div class="col-lg-11">
    <textarea class="form-control message-body-textarea" readonly>${ ironTestUtilsAdatper.prettyPrintJSONOrXML(teststep.request) }</textarea>
  </div>
</div>
<#if stepOtherProperties.rfh2Header??>
  <div class="form-group"></div> <#-- spacer -->
  <div class="row">
    <div class="col-lg-1">
      MQRFH2 Header Folders:
    </div>
    <div class="col-lg-11">
      <#list stepOtherProperties.rfh2Header.folders as mqrfh2Folder>
        <div class="row">
          <div class="col-lg-12">
            <textarea class="form-control" rows="8" readonly>${ ironTestUtilsAdatper.prettyPrintJSONOrXML(mqrfh2Folder.string) }</textarea>
          </div>
        </div>
        <div class="form-group"></div> <#-- spacer -->
      </#list>
    </div>
  </div>
</#if>