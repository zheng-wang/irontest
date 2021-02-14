<textarea class="form-control message-body-textarea" readonly>
  <#t>${ (response.rowsJSON??)?then(response.rowsJSON, response.statementExecutionResults) }
</textarea>
