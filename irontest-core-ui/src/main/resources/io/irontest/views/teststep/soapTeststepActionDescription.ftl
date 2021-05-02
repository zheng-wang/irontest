<#t>Invoke SOAP web service "${ (endpoint.url??)?then(endpoint.url, 'null') }"
<#if endpoint.username??> with username "${ endpoint.username }"</#if>.