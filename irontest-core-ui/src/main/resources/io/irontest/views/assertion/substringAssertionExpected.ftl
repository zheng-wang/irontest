<div class="col-lg-12">
  <div class="row">The response' substring [${ (assertionProperties.beginIndex??)?then(assertionProperties.beginIndex, '') }, ${ (assertionProperties.endIndex??)?then(assertionProperties.endIndex, '') }) is</div>
  <div class="row">
    <textarea class="form-control" rows="6" readonly>${ (assertionProperties.expectedValue??)?then(assertionProperties.expectedValue, 'null') }</textarea>
  </div>
</div>