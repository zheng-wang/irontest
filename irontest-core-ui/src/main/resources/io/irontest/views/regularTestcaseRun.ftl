<div class="row">
  <div class="col-lg-12">
    <ul class="list-unstyled">
      <#list testcaseRun.stepRuns as stepRun>
        <li>
          <a href="#step-run-${ stepRun.id?string.computer }">
            <h5 class="test-result-color-${ stepRun.result }">
              <strong>${ stepRun.teststep.name }</strong>
            </h5>
          </a>
        </li>
      </#list>
    </ul>
  </div>
</div>

<div class="separator"></div>

<#list testcaseRun.stepRuns as stepRun>
  <#include "teststep/stepRun.ftl">
  <div class="row">&nbsp;</div>
</#list>