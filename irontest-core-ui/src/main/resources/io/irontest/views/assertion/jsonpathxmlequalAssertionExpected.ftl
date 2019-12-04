<div class="col-lg-12">
  <div class="row">
    JSONPath "${ verification.assertion.otherProperties.jsonPath }" evaluates to XML:
  </div>
  <div class="row">
    <textarea class="form-control" rows="6" readonly>${ ironTestUtilsAdatper.prettyPrintJSONOrXML(verification.assertion.otherProperties.expectedXML) }</textarea>
  </div>
</div>