<@mqTeststepActionDescription teststep = stepRun.teststep/>

<#macro mqTeststepActionDescription teststep>
  <#local stepOtherProperties = teststep.otherProperties>
  <#local endpointOtherProperties = teststep.endpoint.otherProperties>
  <#if stepOtherProperties.destinationType == "Queue">
    <#if teststep.action == "Clear">
      Clear
    <#elseif teststep.action == "Enqueue">
      Enqueue message
      <#if stepOtherProperties.messageFrom == "Text">
        from text
      <#elseif stepOtherProperties.messageFrom == "File">
        from file "${ stepOtherProperties.messageFilename }"
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
      <#if stepOtherProperties.messageFrom == "Text">
        from text
      <#elseif stepOtherProperties.messageFrom == "File">
        from file "${ stepOtherProperties.messageFilename }"
      </#if>
      onto
    </#if>
    topic with topic string "${ (stepOtherProperties.topicString??)?then(stepOtherProperties.topicString, 'null') }"
  </#if>
  on queue manager "${ endpointOtherProperties.queueManagerAddress }"
  through channel "${ (endpointOtherProperties.svrConnChannelName??)?then(endpointOtherProperties.svrConnChannelName, 'null') }".
</#macro>