<#if stepOtherProperties.destinationType == "Queue">
  <#if teststep.action == "Clear">
    Clear
  <#elseif teststep.action == "Send">
    Send message into
  <#elseif teststep.action == "CheckDepth">
    Check depth of
  <#elseif teststep.action == "Browse">
    Browse
  </#if>
  queue "${ (stepOtherProperties.queueName??)?then(stepOtherProperties.queueName, 'null') }"
<#elseif stepOtherProperties.destinationType == "Topic">
  Publish message onto topic "${ (stepOtherProperties.topicString??)?then(stepOtherProperties.topicString, 'null') }"
</#if>
<#t><#if endpointProperties.jmsProvider == 'Solace'>on Solace router "${ endpoint.constructedUrl }"</#if>
<#if teststep.action == "Browse"> for message at index ${ stepOtherProperties.browseMessageIndex }</#if>.
