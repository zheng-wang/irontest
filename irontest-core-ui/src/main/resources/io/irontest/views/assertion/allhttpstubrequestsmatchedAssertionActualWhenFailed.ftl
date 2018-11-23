<div class="row">
  <div class="col-lg-12">${verification.verificationResult.unmatchedStubRequests?size} requests were not matched:</div>
</div>
<#list verification.verificationResult.unmatchedStubRequests as unmatchedStubRequest>
  <div class="form-group"></div> <!-- spacer -->
  <div class="row">
    <div class="col-lg-12">
      <textarea class="form-control" rows="6" readonly>${ unmatchedStubRequest }</textarea>
    </div>
  </div>
</#list>