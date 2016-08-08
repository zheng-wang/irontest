<#if stepRun.teststep.action == "Clear">
  Clear
<#elseif stepRun.teststep.action == "Enqueue">
  Enqueue message into
<#elseif stepRun.teststep.action == "CheckDepth">
  Check depth of
<#elseif stepRun.teststep.action == "Dequeue">
  Dequeue message from
</#if>
queue "${ stepRun.teststep.otherProperties.queueName }"
on queue manager "${ stepRun.teststep.endpoint.otherProperties.queueManagerAddress }"
through channel "${ stepRun.teststep.endpoint.otherProperties.svrConnChannelName }".