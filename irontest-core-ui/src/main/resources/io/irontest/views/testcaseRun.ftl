<!DOCTYPE html>
<html lang="en">
<head>
  <meta charset="utf-8">
  <meta http-equiv="X-UA-Compatible" content="IE=edge">
  <meta name="viewport" content="width=device-width, initial-scale=1">
  <!-- The above 3 meta tags *must* come first in the head; any other head content must come *after* these tags -->
  <meta name="description" content="">
  <meta name="author" content="">

  <title>Test Report - ${ testcaseRun.testcase.name }</title>

  <#-- Bootstrap CSS; ${bootstrap.version} is Maven property (for filtering) -->
  <style>
    <#include "/META-INF/resources/webjars/bootstrap/${bootstrap.version}/css/bootstrap.min.css">
  </style>

  <!-- Custom CSS -->
  <style>
    .test-result-color-Passed {
      color: lime;
    }
    .test-result-color-Failed {
      color: red;
    }
  </style>
</head>

<body>

<div class="container-fluid">
  <div class="row">
    <div class="col-lg-offset-1 col-lg-10">

      <div class="row">
        <div class="col-lg-12"><h3>${ testcaseRun.testcase.name }</h3></div>
      </div>
      <div class="row">
        <div class="col-lg-1">Start Time:</div>
        <div class="col-lg-2">${ testcaseRun.startTime }</div>
        <div class="col-lg-1">Duration:</div>
        <div class="col-lg-2">${ testcaseRun.duration } ms</div>
      </div>

      <div class="row">&nbsp;</div>

      <#list testcaseRun.stepRuns as stepRun>
        <div class="row"><div class="col-lg-12">${stepRun.teststep.name}</div></div>
        <div class="row"><div class="col-lg-12 test-result-color-${stepRun.result}">${stepRun.result}</div></div>
        <#if stepRun.response??>
          <div class="row">
            <div class="col-lg-1">Response: </div>
            <#include "${stepRun.teststep.type?lower_case}Response.ftl">
          </div>
        </#if>
        <#if stepRun.errorMessage??>
          <div class="row"><div class="col-lg-12">Error message: ${stepRun.errorMessage}</div></div>
        </#if>
        <#list stepRun.assertionVerifications as verification>
          <div class="row">
            <div class="col-lg-1">Assertion:</div>
            <div class="col-lg-11">
              <div class="row">
                <div class="col-lg-2">Verification result:</div>
                <div class="col-lg-1 test-result-color-${verification.verificationResult.result}">
                    ${verification.verificationResult.result}
                </div>
              </div>
              <div class="row">
                <#if verification.verificationResult.error??>
                  <div class="col-lg-2">Error:</div>
                  <div class="col-lg-10">${verification.verificationResult.error}</div>
                <#else>
                  <div class="col-lg-1">Expected:</div>
                  <div class="col-lg-5">
                    <#include "assertion/${verification.assertion.type?lower_case}AssertionExpected.ftl">
                  </div>
                  <div class="col-lg-1">Actual:</div>
                  <div class="col-lg-5">
                    <#if verification.verificationResult.result == "Passed">
                      As expected.
                    <#else>
                      <#include "assertion/${verification.assertion.type?lower_case}AssertionActualWhenFailed.ftl">
                    </#if>
                  </div>
                </#if>
              </div>
            </div>
          </div>
        </#list>

        <div class="row">&nbsp;</div>
      </#list>
    </div>

  </div>
</div>
</body>
</html>
