<#if teststep.action == "CheckDepth">
  ${ response.queueDepth }
<#elseif teststep.action == "Clear">
  Cleared ${ response.clearedMessagesCount } messages.
<#elseif teststep.action == "Browse">
  <#include "jmsTeststepResponseJMSMessage.ftl">
</#if>