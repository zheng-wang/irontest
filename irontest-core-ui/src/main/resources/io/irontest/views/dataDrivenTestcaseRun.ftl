<div class="row">
  <div class="col-lg-12">
    <ul class="list-unstyled">
      <#list testcaseRun.individualRuns as individualRun>
        <li>
          <a href="#testcase-individual-run-${ individualRun.id?string.computer }">
            <h5 class="test-result-color-${ individualRun.result }">
              <strong>[${ individualRun.caption }]</strong>
            </h5>
          </a>
          <ul class="list-unstyled data-driven-testcase-step-list">
            <#list individualRun.stepRuns as stepRun>
              <li>
                <a href="#step-run-${ stepRun.id?string.computer }">
                  <h5 class="test-result-color-${ stepRun.result }">
                    <strong>${ stepRun.teststep.name }</strong>
                  </h5>
                </a>
              </li>
            </#list>
          </ul>
        </li>
      </#list>
    </ul>
  </div>
</div>

<#list testcaseRun.individualRuns as individualRun>
  <div class="separator"></div>

  <div class="row" id="testcase-individual-run-${ individualRun.id?string.computer }">
    <div class="col-lg-11">
      <h4><strong>[${ individualRun.caption }]</strong></h4>
    </div>
    <div class="col-lg-1"><a href="#page-top">Top</a></div>
  </div>

  <div class="row">
    <div class="col-lg-1">Result:</div>
    <div class="col-lg-1 test-result-color-${individualRun.result}">${individualRun.result}</div>
    <div class="col-lg-1">Start Time:</div>
    <div class="col-lg-2">${ individualRun.startTime }</div>
    <div class="col-lg-1">Duration:</div>
    <div class="col-lg-2">${ individualRun.duration } ms</div>
  </div>

  <div class="row">&nbsp;</div>

  <#list individualRun.stepRuns as stepRun>
    <#include "teststep/stepRun.ftl">
    <div class="row">&nbsp;</div>
  </#list>
</#list>






