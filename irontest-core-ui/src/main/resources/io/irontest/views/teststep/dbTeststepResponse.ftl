<textarea class="form-control message-body-textarea" readonly>
  <#t>${ (stepRun.response.rowsJSON??)?then(stepRun.response.rowsJSON, stepRun.response.statementExecutionResults) }
</textarea>
