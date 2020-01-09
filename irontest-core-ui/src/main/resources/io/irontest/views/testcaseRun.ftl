<#ftl encoding='UTF-8'>
<!DOCTYPE html>
<html lang="en">
<head>
  <meta charset="utf-8">
  <meta http-equiv="X-UA-Compatible" content="IE=edge">
  <meta name="viewport" content="width=device-width, initial-scale=1">
  <#-- The above 3 meta tags *must* come first in the head; any other head content must come *after* these tags -->
  <meta name="description" content="">
  <meta name="author" content="">

  <title>Test Report - ${ testcaseRun.testcaseName }</title>

  <#-- Bootstrap CSS; ${bootstrap.version} is Maven property (for filtering) -->
  <style>
    <#include "/META-INF/resources/webjars/bootstrap/${bootstrap.version}/dist/css/bootstrap.min.css">
  </style>
  <#-- Custom CSS -->
  <style>
    <#include "/assets/app/css/html-report.css">
  </style>
</head>

<body>

<#-- jQuery; ${jquery.version} is Maven property (for filtering) -->
<script><#include "/META-INF/resources/webjars/jquery/${jquery.version}/dist/jquery.min.js"></script>
<#-- Bootstrap js; ${bootstrap.version} is Maven property (for filtering) -->
<script><#include "/META-INF/resources/webjars/bootstrap/${bootstrap.version}/dist/js/bootstrap.min.js"></script>

<div class="container-fluid">
  <div class="row">
    <div class="col-lg-offset-1 col-lg-10">

      <div class="row" id="page-top">
        <div class="col-lg-12"><h3>${ testcaseRun.testcaseFolderPath }/${ testcaseRun.testcaseName }</h3></div>
      </div>

      <div class="row">
        <div class="col-lg-1">Result:</div>
        <div class="col-lg-1 test-result-color-${testcaseRun.result}">${testcaseRun.result}</div>
        <div class="col-lg-1">Start Time:</div>
        <div class="col-lg-2">${ testcaseRun.startTime }</div>
        <div class="col-lg-1">Duration:</div>
        <div class="col-lg-2">${ testcaseRun.duration } ms</div>
      </div>

      <div class="row">&nbsp;</div>

      <#if testcaseRun.stepRuns??>
        <#include "regularTestcaseRun.ftl">
      <#elseif testcaseRun.individualRuns??>
        <#include "dataDrivenTestcaseRun.ftl">
      </#if>
    </div>
  </div>
</div>
</body>
</html>
