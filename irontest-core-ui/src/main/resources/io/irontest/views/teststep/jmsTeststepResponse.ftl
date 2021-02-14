<#if teststep.action == "CheckDepth">
  ${ response.queueDepth }
<#elseif teststep.action == "Clear">
  Cleared ${ response.clearedMessagesCount } messages.
</#if>