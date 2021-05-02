<#if stepOtherProperties.destinationType == "Queue">
  <#if teststep.action == "Clear">
    Clear
  <#elseif teststep.action == "Enqueue">
    Enqueue message
    <#if teststep.requestType == "Text">
      from text
    <#elseif teststep.requestType == "File">
      from file "${ teststep.requestFilename }"
    </#if>
    into
  <#elseif teststep.action == "CheckDepth">
    Check depth of
  <#elseif teststep.action == "Dequeue">
    Dequeue message from
  </#if>
  queue "${ (stepOtherProperties.queueName??)?then(stepOtherProperties.queueName, 'null') }"
<#elseif stepOtherProperties.destinationType == "Topic">
  Publish message
  <#if teststep.requestType == "Text">
    from text
  <#elseif teststep.requestType == "File">
    from file "${ teststep.requestFilename }"
  </#if>
  onto topic with topic string "${ (stepOtherProperties.topicString??)?then(stepOtherProperties.topicString, 'null') }"
</#if>
<#t>on queue manager "${ teststep.endpoint.constructedUrl }"
<#t><#if endpointProperties.connectionMode == "Client"> through channel "${ (endpointProperties.svrConnChannelName??)?then(endpointProperties.svrConnChannelName, 'null') }"</#if>.