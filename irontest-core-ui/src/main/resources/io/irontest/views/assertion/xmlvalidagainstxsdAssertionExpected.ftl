Response conforms to XSD(s)
<#if assertionProperties.fileBytes??>
  <#assign mimeType = (assertionProperties.fileName?lower_case?ends_with('.xsd'))?then('text/plain', 'application/zip')>
  <a href="data:${ mimeType };base64,${ ironTestUtilsAdatper.base64EncodeByteArray(assertionProperties.fileBytes) }"
    download="${ assertionProperties.fileName }">${ assertionProperties.fileName }</a>.
<#else>
  null.
</#if>