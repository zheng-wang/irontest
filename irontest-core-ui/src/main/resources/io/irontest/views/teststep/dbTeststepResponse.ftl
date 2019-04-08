<textarea class="form-control" rows="8" readonly>
  <#t>${ (stepRun.response.rowsJSON??)?then(stepRun.response.rowsJSON, stepRun.response.statementExecutionResults) }
</textarea>
