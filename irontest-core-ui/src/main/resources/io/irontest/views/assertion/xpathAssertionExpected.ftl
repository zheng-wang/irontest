<div class="col-lg-12">
  <div class="row">XPath "${ (verification.assertion.otherProperties.xPath??)?then(verification.assertion.otherProperties.xPath, 'null') }" evaluates to</div>
  <div class="row">
    <textarea class="form-control" rows="6" readonly>${ (verification.assertion.otherProperties.expectedValue??)?then(verification.assertion.otherProperties.expectedValue, 'null') }</textarea>
  </div>
</div>