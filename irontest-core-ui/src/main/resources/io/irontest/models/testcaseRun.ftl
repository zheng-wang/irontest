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

<div class="container">
  <div class="row"><div class="col-lg-12"><h3>${ testcaseRun.testcase.name }</h3></div>
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
      <div class="row">Error message: ${stepRun.errorMessage}</div>
    </#if>
    <#list stepRun.assertionVerifications as verification>
      <div class="row">
        <div class="col-lg-1">Assertion:</div>
        <div class="col-lg-11">
          <div class="row">
            <div class="col-lg-2">Verification result:</div>
            <div class="test-result-color-${verification.verificationResult.result}">
                ${verification.verificationResult.result}
            </div>
          </div>
          <#if verification.verificationResult.error??>
            <div class="row">
              <div class="col-lg-2">Verification error:</div>
              <div>${verification.verificationResult.error}</div>
            </div>
          </#if>
        </div>
      </div>
    </#list>

    <div class="row">&nbsp;</div>
  </#list>
</div>

</body>
</html>
