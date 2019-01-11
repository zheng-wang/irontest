<#import "../formatHTTPHeadersObj.ftl" as fmt>

<div class="row">
  <div class="col-lg-12">Reset mock server, and load stubs:</div>
</div>
<#list stepRun.teststep.otherProperties.httpStubMappings as stubMapping>
  <div class="form-group"></div> <!-- spacer -->
  <div class="row">
    <div class="col-lg-1">${ stubMapping.number }</div>
    <form name="stubSpecForm" class="form-horizontal col-lg-11" role="form" novalidate>
      <div class="form-group">
        <div class="col-lg-1">Method:</div>
        <div class="col-lg-1">${ stubMapping.spec.request.method }</div>
        <div class="col-lg-1">URL:</div>
        <div class="col-lg-8">${ stubMapping.spec.request.url }</div>
      </div>
      <div class="form-group">
        <div class="col-lg-offset-6 col-lg-2">Response Status Code:</div>
        <div class="col-lg-1">${ stubMapping.spec.response.status }</div>
        <#if stubMapping.spec.response.fixedDelayMilliseconds??>
          <div class="col-lg-1">Delay (ms):</div>
          <div class="col-lg-1">${ stubMapping.spec.response.fixedDelayMilliseconds }</div>
        </#if>
      </div>
      <div class="form-group">
        <div class="col-lg-6">Request Headers:</div>
        <div class="col-lg-6">Response Headers:</div>
      </div>
      <div class="form-group">
        <div class="col-lg-6">
          <textarea name="requestHeaders" rows="6" class="form-control" readonly>
            <#if stubMapping.spec.request.headers??>
              <#compress>
                <#list stubMapping.spec.request.headers?keys as key>
                  ${key}: ${stubMapping.spec.request.headers[key].expected }
                </#list>
              </#compress>
            </#if>
          <#lt></textarea>
        </div>
        <div class="col-lg-6">
          <textarea name="responseHeaders" rows="6" class="form-control" readonly><#if stubMapping.spec.response.headers??><@fmt.formatHTTPHeadersObj object=stubMapping.spec.response.headers/></#if></textarea>
        </div>
      </div>
      <div class="form-group">
        <div class="col-lg-2"><#if stubMapping.spec.request.method == "POST" || stubMapping.spec.request.method == "PUT">Request Body</#if></div>
        <div class="col-lg-2">
          <#if stubMapping.spec.request.bodyPatterns??>
            <#if (stubMapping.spec.request.bodyPatterns?first).equalToXml??>Equal to XML</#if>
            <#if (stubMapping.spec.request.bodyPatterns?first).equalToJson??>Equal to JSON</#if>
          <#elseif stubMapping.spec.request.method == "POST" || stubMapping.spec.request.method == "PUT">Can be Any
          </#if>
        </div>
        <div class="col-lg-offset-2 col-lg-6">Response Body:</div>
      </div>
      <div class="form-group">
        <div class="col-lg-6">
          <#if stubMapping.spec.request.bodyPatterns??>
            <textarea name="requestBody" rows="9" class="form-control" readonly>${ (stubMapping.spec.request.bodyPatterns?first).value }</textarea>
          </#if>
        </div>
        <div class="col-lg-6">
          <textarea name="responseBody" rows="9" class="form-control" readonly>${ (stubMapping.spec.response.body??)?then(stubMapping.spec.response.body, '') }</textarea>
        </div>
      </div>
    </form>
  </div>
</#list>