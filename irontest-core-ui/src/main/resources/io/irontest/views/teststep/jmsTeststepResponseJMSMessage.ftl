<div>
  <#-- Nav tabs -->
  <ul class="nav nav-tabs tabs-in-test-report" role="tablist">
    <#-- use data-target attribute instead of href attribute on the anchor elements, to avoid spoiling routes of
      angular app on the test case edit view. Refer to https://stackoverflow.com/questions/19225968/bootstrap-tab-is-not-working-when-tab-with-data-target-instead-of-href for more details -->
    <li role="presentation"><a data-target="#step-run-${ stepRun.id?string.computer }-response-header" aria-controls="header" role="tab" data-toggle="tab">Header</a></li>
    <li role="presentation"><a data-target="#step-run-${ stepRun.id?string.computer }-response-properties" aria-controls="properties" role="tab" data-toggle="tab">Properties</a></li>
    <#-- set Body tab to be active as Body is the most interesting information -->
    <li role="presentation" class="active"><a data-target="#step-run-${ stepRun.id?string.computer }-response-body" aria-controls="body" role="tab" data-toggle="tab">Body</a></li>
  </ul>

  <#-- Tab panes -->
  <div class="tab-content">
    <#-- Header tab pane -->
    <div role="tabpanel" class="tab-pane" id="step-run-${ stepRun.id?string.computer }-response-header">
      <div class="form-group"></div> <#-- spacer -->
      <div class="form-group">
        <div class="col-lg-8">
          <table class="table table-condensed table-bordered table-hover">
            <thead>
              <tr>
                <th width="25%">Name</th>
                <th>Value</th>
              </tr>
            </thead>
            <tbody>
              <#list response.header as headerFieldName, headerFieldValue>
                <tr>
                  <td>${ headerFieldName }</td>
                  <td>${ (headerFieldValue??)?then(headerFieldValue, '') }</td>
                </tr>
              </#list>
            </tbody>
          </table>
        </div>
      </div>
    </div>
    <#-- Properties tab pane -->
    <div role="tabpanel" class="tab-pane" id="step-run-${ stepRun.id?string.computer }-response-properties">
      <div class="form-group"></div> <#-- spacer -->
      <div class="form-group">
        <div class="col-lg-8">
          <table class="table table-condensed table-bordered table-hover">
            <thead>
              <tr>
                <th width="25%">Name</th>
                <th>Value</th>
              </tr>
            </thead>
            <tbody>
              <#list response.properties as propertyName, propertyValue>
                <tr>
                  <td>${ propertyName }</td>
                  <td>${ (propertyValue??)?then(propertyValue, '') }</td>
                </tr>
              </#list>
            </tbody>
          </table>
        </div>
      </div>
    </div>
    <#-- Body tab pane -->
    <div role="tabpanel" class="tab-pane active" id="step-run-${ stepRun.id?string.computer }-response-body">
      <div class="form-group">
        <div class="col-lg-12">
          <textarea class="form-control message-body-textarea" readonly>${ ironTestUtilsAdatper.prettyPrintJSONOrXML(response.body) }</textarea>
        </div>
      </div>
    </div>
  </div>
</div>