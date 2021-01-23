<@mqTeststepActionDescription teststep = stepRun.teststep/>

<#macro mqTeststepActionDescription teststep>
  <#local stepOtherProperties = teststep.otherProperties>
  <#local endpointOtherProperties = teststep.endpoint.otherProperties>
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
    <#if teststep.action == "Publish">
      Publish message
      <#if teststep.requestType == "Text">
        from text
      <#elseif teststep.requestType == "File">
        from file "${ teststep.requestFilename }"
      </#if>
      onto
    </#if>
    topic with topic string "${ (stepOtherProperties.topicString??)?then(stepOtherProperties.topicString, 'null') }"
  </#if>
  <#t>on queue manager "${ teststep.endpoint.url }"
  <#t><#if endpointOtherProperties.connectionMode == "Client"> through channel "${ (endpointOtherProperties.svrConnChannelName??)?then(endpointOtherProperties.svrConnChannelName, 'null') }"</#if>.
</#macro>