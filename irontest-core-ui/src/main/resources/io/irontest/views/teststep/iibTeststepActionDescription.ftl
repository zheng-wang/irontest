${stepRun.teststep.action} message flow "${ stepRun.teststep.otherProperties.messageFlowName }"
on EG "${ stepRun.teststep.otherProperties.integrationServerName }"
on broker "${ stepRun.teststep.endpoint.otherProperties.queueManagerAddress }"
through channel "${ stepRun.teststep.endpoint.otherProperties.svrConnChannelName }".