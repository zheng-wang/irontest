<@mqTeststepActionDescription teststep = stepRun.teststep/>

<#macro mqTeststepActionDescription teststep>
  <#local stepOtherProperties = teststep.otherProperties>
  <#local endpointOtherProperties = teststep.endpoint.otherProperties>
  <#if teststep.action == "Clear">
    Clear
  <#elseif teststep.action == "Enqueue">
    Enqueue message
    <#if stepOtherProperties.enqueueMessageFrom == "Text">
      from text
    <#elseif stepOtherProperties.enqueueMessageFrom == "File">
      from file "${ stepOtherProperties.enqueueMessageFilename }"
    </#if>
    into
  <#elseif teststep.action == "CheckDepth">
    Check depth of
  <#elseif teststep.action == "Dequeue">
    Dequeue message from
  </#if>
  queue "${ stepOtherProperties.queueName }"
  on queue manager "${ endpointOtherProperties.queueManagerAddress }"
  through channel "${ (endpointOtherProperties.svrConnChannelName??)?then(endpointOtherProperties.svrConnChannelName, 'null') }".
</#macro>