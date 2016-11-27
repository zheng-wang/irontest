Run SQL statement(s) on database "${ stepRun.teststep.endpoint.url }"
<#if stepRun.teststep.endpoint.username??>with username "${ stepRun.teststep.endpoint.username }"</#if>.