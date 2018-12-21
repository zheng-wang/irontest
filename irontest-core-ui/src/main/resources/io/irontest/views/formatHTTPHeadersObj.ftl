<#-- object must be of type com.github.tomakehurst.wiremock.http.HttpHeaders.
     This macro is created because HttpHeaders.toString() outputs header value wrapped in [].
-->
<#macro formatHTTPHeadersObj object>
  <#compress>
    <#list object.all() as header>
      ${ header.key() }: ${ header.firstValue() }
    </#list>
  </#compress>
</#macro>