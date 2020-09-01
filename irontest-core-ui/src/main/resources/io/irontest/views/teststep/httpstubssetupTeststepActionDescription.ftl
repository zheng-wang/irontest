<#import "../formatHTTPHeadersObj.ftl" as fmt>

<div class="row">
  <div class="col-lg-12">Reset mock server, and load stubs:</div>
</div>
<#list stepRun.teststep.otherProperties.httpStubMappings as stubMapping>
  <#assign stubSpec = stubMapping.spec>

  <div class="form-group"></div> <#-- spacer -->
  <div class="row">
    <div class="col-lg-1">${ stubMapping.number }</div>
    <form name="stubSpecForm" class="form-horizontal col-lg-11" role="form" novalidate>
      <#if stubSpec.scenarioName??>
        <div class="form-group">
          <div class="col-lg-2">Scenario Name:</div>
          <div class="col-lg-2">${ stubSpec.scenarioName }</div>
          <div class="col-lg-2">Required Scenario State:</div>
          <div class="col-lg-2">${ stubSpec.requiredScenarioState }</div>
          <#if stubSpec.newScenarioState??>
            <div class="col-lg-2">New Scenario State:</div>
            <div class="col-lg-2">${ stubSpec.newScenarioState }</div>
          </#if>
        </div>
      </#if>
      <div class="form-group">
        <div class="col-lg-1">Method:</div>
        <div class="col-lg-1">${ stubSpec.request.method }</div>
        <#if stubSpec.request.urlPattern??>
          <div class="col-lg-2">URL (Regex Matching):</div>
          <div class="col-lg-8">${ stubSpec.request.urlPattern }</div>
        <#else>
          <div class="col-lg-1">URL:</div>
          <div class="col-lg-8">${ stubSpec.request.url }</div>
        </#if>
      </div>
      <div class="form-group">
        <div class="col-lg-offset-6 col-lg-2">Response Status Code:</div>
        <div class="col-lg-1">${ stubSpec.response.status }</div>
        <#if stubSpec.response.fixedDelayMilliseconds??>
          <div class="col-lg-1">Delay (ms):</div>
          <div class="col-lg-1">${ stubSpec.response.fixedDelayMilliseconds }</div>
        </#if>
      </div>
      <div class="form-group">
        <div class="col-lg-6">Request Headers:</div>
        <div class="col-lg-6">Response Headers:</div>
      </div>
      <div class="form-group">
        <div class="col-lg-6">
          <textarea name="requestHeaders" rows="6" class="form-control" readonly>
            <#if stubSpec.request.headers??>
              <#assign requestHeaders = stubSpec.request.headers>
              <#compress>
                <#list requestHeaders?keys as key>
                  <#t>${ key }
                  <#if requestHeaders[key].name = 'anything'>
                    is anything
                  <#else>
                    ${ requestHeaders[key].name } ${ requestHeaders[key].expected }
                  </#if>
                </#list>
              </#compress>
            </#if>
          <#lt></textarea>
        </div>
        <div class="col-lg-6">
          <textarea name="responseHeaders" rows="6" class="form-control" readonly><#if stubSpec.response.headers??><@fmt.formatHTTPHeadersObj object=stubSpec.response.headers/></#if></textarea>
        </div>
      </div>
      <div class="form-group">
        <div class="col-lg-2"><#if stubSpec.request.method == "POST" || stubSpec.request.method == "PUT">Request Body</#if></div>
        <div class="col-lg-2">
          <#if stubSpec.request.bodyPatterns??>
            <#if (stubSpec.request.bodyPatterns?first).equalToXml??>Equal to XML</#if>
            <#if (stubSpec.request.bodyPatterns?first).equalToJson??>Equal to JSON</#if>
          <#elseif stubSpec.request.method == "POST" || stubSpec.request.method == "PUT">Can be Any
          </#if>
        </div>
        <div class="col-lg-offset-2 col-lg-6">Response Body:</div>
      </div>
      <div class="form-group">
        <div class="col-lg-6">
          <#if stubSpec.request.bodyPatterns??>
            <textarea name="requestBody" rows="9" class="form-control" readonly>${ ironTestUtilsAdatper.prettyPrintJSONOrXML((stubSpec.request.bodyPatterns?first).value) }</textarea>
          </#if>
        </div>
        <div class="col-lg-6">
          <textarea name="responseBody" rows="9" class="form-control" readonly>${ ironTestUtilsAdatper.prettyPrintJSONOrXML((stubSpec.response.body??)?then(stubSpec.response.body, '')) }</textarea>
        </div>
      </div>
    </form>
  </div>
</#list>