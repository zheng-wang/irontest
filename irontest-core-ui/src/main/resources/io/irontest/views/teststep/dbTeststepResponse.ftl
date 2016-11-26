<div class="row">
  <div class="col-lg-2">Non-Select Statements Execution Result:</div>
  <div>${stepRun.response.statementExecutionResults}</div>
</div>
<div class="row">
  <div class="col-lg-2">Select Statement Execution Result Set:</div>
  <div class="col-lg-10">
    <#list stepRun.response.resultSet as row>
      <div class="row">{
        <#list row?keys as columnName>
          <#if (columnName?index > 0)>, </#if>
          ${columnName} : "${row[columnName]}"
        </#list>
        }
      </div>
    </#list>
  </div>
</div>
