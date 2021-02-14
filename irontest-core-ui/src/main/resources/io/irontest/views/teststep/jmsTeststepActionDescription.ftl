<#if stepOtherProperties.destinationType == "Queue">
  <#if teststep.action == "Clear">
    Clear
  <#elseif teststep.action == "Send">
    Send message
    <#if teststep.requestType == "Text">
      from text
    </#if>
    into
  <#elseif teststep.action == "CheckDepth">
    Check depth of
  <#elseif teststep.action == "Browse">
    Browse message from
  </#if>
  queue "${ (stepOtherProperties.queueName??)?then(stepOtherProperties.queueName, 'null') }"
<#elseif stepOtherProperties.destinationType == "Topic">
  <#if teststep.action == "Publish">
    Publish message
    <#if teststep.requestType == "Text">
      from text
    </#if>
    onto
  </#if>
  topic "${ (stepOtherProperties.topicString??)?then(stepOtherProperties.topicString, 'null') }"
</#if>
<#t><#if endpointProperties.jmsProvider == 'Solace'>on Solace router "${ endpoint.constructedUrl }"</#if>.
