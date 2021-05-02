Run SQL statement(s) on database with JDBC URL "${ (endpoint.url??)?then(endpoint.url, 'null') }"
<#if endpoint.username??>and username "${ endpoint.username }"</#if>.