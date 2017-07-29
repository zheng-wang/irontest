<#if stepRun.teststep.action == "WaitForProcessingCompletion">Wait for processing completion of<#else>${stepRun.teststep.action}</#if>
 message flow "${ stepRun.teststep.otherProperties.messageFlowName }"
on integration server "${ stepRun.teststep.otherProperties.integrationServerName }"
on integration node "${ (stepRun.teststep.endpoint.type == 'MQ'??)?then(stepRun.teststep.endpoint.otherProperties.queueManagerAddress, stepRun.teststep.endpoint.otherProperties.integrationNodeAddress) }"
<#if stepRun.teststep.endpoint.type == 'MQ'>through channel "${ stepRun.teststep.endpoint.otherProperties.svrConnChannelName }".</#if>