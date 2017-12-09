<#if stepRun.teststep.action == 'WaitForProcessingCompletion'>Wait for processing completion of<#else>${stepRun.teststep.action}</#if>
 message flow "${ stepRun.teststep.otherProperties.messageFlowName }"
<#if stepRun.teststep.otherProperties.applicationName??>in application "${stepRun.teststep.otherProperties.applicationName}"</#if>
on integration server "${ stepRun.teststep.otherProperties.integrationServerName }"
<#t>on integration node "${ (stepRun.teststep.endpoint.type == 'MQ')?then(stepRun.teststep.endpoint.otherProperties.queueManagerAddress, stepRun.teststep.endpoint.otherProperties.integrationNodeAddress) }"
<#if stepRun.teststep.endpoint.type == 'MQ' && stepRun.teststep.endpoint.otherProperties.connectionMode == 'Client'> through channel "${ (stepRun.teststep.endpoint.otherProperties.svrConnChannelName??)?then(stepRun.teststep.endpoint.otherProperties.svrConnChannelName, 'null') }"</#if>.