<#if stepRun.teststep.action == "WaitForProcessingCompletion">Wait for processing completion of<#else>${stepRun.teststep.action}</#if>
 message flow "${ stepRun.teststep.otherProperties.messageFlowName }"
on EG "${ stepRun.teststep.otherProperties.integrationServerName }"
on broker "${ stepRun.teststep.endpoint.otherProperties.queueManagerAddress }"
through channel "${ stepRun.teststep.endpoint.otherProperties.svrConnChannelName }".