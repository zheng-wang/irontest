<div class="col-lg-12">
  <div class="row">The response is matched by regex</div>
  <div class="row">
    <textarea class="form-control" rows="6" readonly>${ (assertionProperties.regex??)?then(assertionProperties.regex, 'null') }</textarea>
  </div>
</div>