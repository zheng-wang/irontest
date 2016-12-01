<textarea class="form-control" rows="8" readonly>
<#t><#if stepRun.response.resultSet??>
    <#list stepRun.response.resultSet as row>
      <#t>{<#list row?keys as columnName><#if (columnName?index > 0)>, </#if>${ columnName } : ${ row[columnName]???then('"' + row[columnName] + '"', 'null') }  <#-- Not real JSON serialization. For example, number is also surrounded by double quotes. -->
      </#list>}
    </#list>
  <#else>${ stepRun.response.statementExecutionResults }
  </#if>
</textarea>