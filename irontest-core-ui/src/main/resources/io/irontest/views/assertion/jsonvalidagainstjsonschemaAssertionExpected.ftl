Response conforms to JSON Schema
<#if assertionProperties.fileBytes??>
  <a href="data:text/plain;base64,${ ironTestUtilsAdatper.base64EncodeByteArray(assertionProperties.fileBytes) }"
    download="${ assertionProperties.fileName }">${ assertionProperties.fileName }</a>.
<#else>
  null.
</#if>