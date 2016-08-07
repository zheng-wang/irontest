<div class="col-lg-11">
  <div class="row">
    <div class="col-lg-2">numberOfRowsModified:</div>
    <div>${stepRun.response.numberOfRowsModified}</div>
  </div>
  <div class="row">
    <div class="col-lg-2">resultSet:</div>
    <div class="col-lg-10">
      <#list stepRun.response.resultSet as row>
        <div class="row">{
          <#list row?keys as columnName>
            <#if (columnName?index > 0)>, </#if>
            ${columnName} : "${row[columnName]}"
          </#list>
          }
        </div>
      </#list>
    </div>
  </div>
</div>
