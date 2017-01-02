<textarea class="form-control" rows="8" readonly>
<#t><#if stepRun.response.rowsJSON??>
      <#t>${ stepRun.response.rowsJSON }
  <#else>${ stepRun.response.statementExecutionResults }
  </#if>
</textarea>