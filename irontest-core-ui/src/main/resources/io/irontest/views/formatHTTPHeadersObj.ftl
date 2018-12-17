<#macro formatHTTPHeadersObj object>
  <#compress>
    <#list object?keys as key>
      <#lt>${key}: ${object[key]}
    </#list>
  </#compress>
</#macro>