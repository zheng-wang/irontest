<#-- Extra response info for test step that has response HTTP headers -->
<#if stepRun.response.httpHeaders?? && (stepRun.response.httpHeaders?size > 0)>
  <div class="row">
    <div class="col-lg-2">HTTP Headers:</div>
    <div class="col-lg-10">
      <#list stepRun.response.httpHeaders as httpHeader>
        <div class="row">
          <div class="col-lg-2">${ httpHeader.name }:</div>
          <div class="col-lg-10">${ httpHeader.value }</div>
        </div>
      </#list>
    </div>
  </div>
</#if>