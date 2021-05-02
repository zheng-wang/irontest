<#if teststep.action == 'WaitForProcessingCompletion'>Wait for processing completion of<#else>${teststep.action}</#if>
 message flow "${ stepOtherProperties.messageFlowName }"
<#if stepOtherProperties.applicationName??>in application "${stepOtherProperties.applicationName}"</#if>
on integration server "${ stepOtherProperties.integrationServerName }"
<#t>on integration node "${ endpoint.constructedUrl }"
<#if endpoint.type == 'MQ' && endpointProperties.connectionMode == 'Client'> through channel "${ (endpointProperties.svrConnChannelName??)?then(endpointProperties.svrConnChannelName, 'null') }"</#if>.