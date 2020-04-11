It was
<#if verificationResult.actualHitCount = 0>
  not hit.
<#else>
  hit ${ verificationResult.actualHitCount } times.
</#if>