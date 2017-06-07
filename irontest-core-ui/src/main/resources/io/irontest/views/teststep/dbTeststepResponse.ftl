<#t><#if stepRun.response.rowsJSON??>
      <#t>${ stepRun.response.rowsJSON }
  <#else>${ stepRun.response.statementExecutionResults }
  </#if>