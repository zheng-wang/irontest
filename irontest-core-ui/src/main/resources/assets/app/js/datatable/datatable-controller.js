'use strict';

//  NOTICE:
//    The $scope here prototypically inherits from the $scope of TestcasesController,
angular.module('irontest').controller('DataTableController', ['$scope', 'IronTestUtils', '$stateParams', 'DataTable',
  function($scope, IronTestUtils, $stateParams, DataTable) {
    $scope.dataTableGridOptions = {
      enableFiltering: true, enableSorting: false
    };

    $scope.findByTestcaseId = function() {
      DataTable.get({ testcaseId: $stateParams.testcaseId }, function(dataTable) {
        $scope.dataTable = dataTable;

        $scope.dataTableGridOptions.columnDefs = [];
        for (var i = 0; i < dataTable.columns.length; i++) {
          var dataTableColumn = dataTable.columns[i];
          var dataTableColumnName = dataTableColumn.name;
          var uiGridColumn = {
            name: dataTableColumnName,
            displayName: dataTableColumnName,  //  need this line to avoid underscore in column name is not displayed in column header
            // determine column min width according to the length of column name
            // assuming each character deserves 8 pixels
            // 30 pixels for displaying grid header menu arrow
            minWidth: dataTableColumnName.length * 8 + 30
          };
          if (dataTableColumn.type !== 'String') {    //  it is an endpoint column
            uiGridColumn.cellTemplate = 'dataTableGridEndpointTypedCellTemplate.html';
          }
          $scope.dataTableGridOptions.columnDefs.push(uiGridColumn);
        }
        $scope.dataTableGridOptions.data = dataTable.rows;

      }, function(response) {
        IronTestUtils.openErrorHTTPResponseModal(response);
      });
    };
  }
]);