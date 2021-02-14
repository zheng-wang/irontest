<#if teststep.action == "CheckDepth">
  ${ response.queueDepth }
<#elseif teststep.action == "Dequeue">
  <div class="row">
    <div class="col-lg-1">
      Body:
    </div>
    <div class="col-lg-11">
      <textarea class="form-control message-body-textarea" readonly>${ ironTestUtilsAdatper.prettyPrintJSONOrXML(response.bodyAsText) }</textarea>
    </div>
  </div>
  <#if response.mqrfh2Header??>
    <div class="form-group"></div> <#-- spacer -->
    <div class="row">
      <div class="col-lg-1">
        MQRFH2 Header Folders:
      </div>
      <div class="col-lg-11">
        <#list response.mqrfh2Header.folders as mqrfh2Folder>
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
</#if>