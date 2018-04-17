'use strict';

//  NOTICE:
//    The $scope here prototypically inherits from the $scope of TestcasesController,
angular.module('irontest').controller('DataTableController', ['$scope', 'IronTestUtils', '$stateParams', 'DataTable',
    '$timeout',
  function($scope, IronTestUtils, $stateParams, DataTable, $timeout) {
    $scope.dataTableGridRefresh = false;
    $scope.dataTableGridOptions = {
      enableSorting: false
    };

    var updateDataTableGridOptions = function(dataTable, lastColumnHeaderInEditMode) {
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
        if (lastColumnHeaderInEditMode === true && i === dataTable.columns.length - 1) {
          uiGridColumn.headerCellTemplate = 'dataTableGridEditableHeaderCellTemplate.html';
        }
        if (dataTableColumn.type !== 'String') {    //  it is an endpoint column
          uiGridColumn.cellTemplate = 'dataTableGridEndpointTypedCellTemplate.html';
        }
        $scope.dataTableGridOptions.columnDefs.push(uiGridColumn);
      }
      $scope.dataTableGridOptions.data = dataTable.rows;
    };

    $scope.findByTestcaseId = function(callbackOnSuccess) {
      DataTable.get({ testcaseId: $stateParams.testcaseId }, function(dataTable) {
        updateDataTableGridOptions(dataTable);
        if (callbackOnSuccess) {
          callbackOnSuccess();
        }
      }, function(response) {
        IronTestUtils.openErrorHTTPResponseModal(response);
      });
    };

    $scope.addColumn = function(columnType) {
      DataTable.addColumn({ testcaseId: $stateParams.testcaseId, columnType: columnType }, {}, function(dataTable) {
        updateDataTableGridOptions(dataTable, true);
      }, function(response) {
        IronTestUtils.openErrorHTTPResponseModal(response);
      });
    };

    $scope.afterColumnNameEdit = function(originalName, newName) {
      if (newName !== originalName) {

      } else {
        $scope.findByTestcaseId(function() {
          //  reload the grid on UI, so that column edit mode is exited
          $scope.dataTableGridRefresh = true;
          $timeout(function() {
            $scope.dataTableGridRefresh = false;
          }, 0);
        });
      }
    };
  }
]);