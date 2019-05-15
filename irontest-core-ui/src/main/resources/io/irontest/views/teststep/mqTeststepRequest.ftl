<#if stepRun.teststep.action == "Enqueue" || stepRun.teststep.action == "Publish">
  <div class="row">
    <div class="col-lg-1">
      Body:
    </div>
    <div class="col-lg-11">
      <textarea class="form-control" rows="8" readonly>${ stepRun.teststep.request }</textarea>
    </div>
  </div>
  <#if stepRun.teststep.otherProperties.rfh2Header??>
    <div class="form-group"></div> <!-- spacer -->
    <div class="row">
      <div class="col-lg-1">
        MQRFH2 Header Folders:
      </div>
      <div class="col-lg-11">
        <#list stepRun.teststep.otherProperties.rfh2Header.folders as mqrfh2Folder>
          <div class="row">
            <div class="col-lg-12">
              <textarea class="form-control" rows="8" readonly>${ mqrfh2Folder.string }</textarea>
            </div>
          </div>
          <div class="form-group"></div> <!-- spacer -->
        </#list>
      </div>
    </div>
  </#if>
<#else>
  <textarea class="form-control" rows="8" readonly>${ stepRun.teststep.request }</textarea>
</#if>
