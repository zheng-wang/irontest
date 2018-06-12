<div class="row">
  <div class="col-lg-12">
    <ul class="list-unstyled">
      <#list testcaseRun.individualRuns as individualRun>
        <li>
          <a href="#individual-run-${ individualRun.id?string.computer }">
            <h5 class="test-result-color-${ individualRun.result }">
              <strong>[${ individualRun.caption }]</strong>
            </h5>
          </a>
          <ul class="list-unstyled">
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

<div class="row">
  <div class="col-lg-12">
    <div style="border-bottom: 1px solid black">&nbsp;</div>
  </div>
</div>

