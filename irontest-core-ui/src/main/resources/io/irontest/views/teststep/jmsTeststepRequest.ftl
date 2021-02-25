<div class="form-group"></div> <#-- spacer -->

<div>
  <#-- Nav tabs -->
  <ul class="nav nav-tabs tabs-in-test-report" role="tablist">
    <#-- use data-target attribute instead of href attribute on the anchor elements, to avoid spoiling routes of
      angular app on the test case edit view. Refer to https://stackoverflow.com/questions/19225968/bootstrap-tab-is-not-working-when-tab-with-data-target-instead-of-href for more details -->
    <li role="presentation"><a data-target="#step-run-${ stepRun.id?string.computer }-request-properties" aria-controls="properties" role="tab" data-toggle="tab">Properties</a></li>
    <#-- set Body tab to be active as Body is the most interesting information -->
    <li role="presentation" class="active"><a data-target="#step-run-${ stepRun.id?string.computer }-request-body" aria-controls="body" role="tab" data-toggle="tab">Body</a></li>
  </ul>

  <#-- Tab panes -->
  <div class="tab-content">
    <div role="tabpanel" class="tab-pane" id="step-run-${ stepRun.id?string.computer }-request-properties">
      Properties
    </div>
    <div role="tabpanel" class="tab-pane active" id="step-run-${ stepRun.id?string.computer }-request-body">
      Body
    </div>
  </div>
</div>