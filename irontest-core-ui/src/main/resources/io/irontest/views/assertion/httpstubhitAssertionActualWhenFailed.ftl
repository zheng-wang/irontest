It was
<#if verification.verificationResult.actualHitCount = 0>
  not hit.
<#else>
  hit ${verification.verificationResult.actualHitCount} times.
</#if>