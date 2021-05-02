<#t>Invoke HTTP API "${ (endpoint.url??)?then(endpoint.url, 'null') }" using method ${ stepOtherProperties.httpMethod }
<#if endpoint.username??> with username "${ endpoint.username }"</#if>.