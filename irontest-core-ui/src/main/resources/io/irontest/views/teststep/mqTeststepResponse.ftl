<#if stepRun.teststep.action == "CheckDepth">
  ${stepRun.response.queueDepth}
<#elseif stepRun.teststep.action == "Dequeue">
  <textarea class="form-control" rows="8" readonly>${stepRun.response.bodyAsText}</textarea>
</#if>