'use strict';

//  NOTICE:
//    The $scope here prototypically inherits from the $scope of TestcasesController,
angular.module('irontest').controller('DataTableController', ['$scope', 'IronTestUtils', '$stateParams', 'DataTable',
    '$timeout',
  function($scope, IronTestUtils, $stateParams, DataTable, $timeout) {
    $scope.dataTableGridOptions = {
      enableSorting: false
    };

    var getDefaultColumnDef = function(dataTableColumnId, columnName) {
      return {
        dataTableColumnId: dataTableColumnId,    //  not standard ui grid property for column def
        name: columnName,
        displayName: columnName,  //  need this line to avoid underscore in column name is not displayed in column header
        // determine column min width according to the length of column name
        // assuming each character deserves 8 pixels
        // 30 pixels for displaying grid header menu arrow
        minWidth: columnName.length * 8 + 30
      };
    };

    var updateDataTableGrid = function(dataTable, lastColumnHeaderInEditMode) {
      $scope.dataTableGridOptions.columnDefs = [];
      for (var i = 0; i < dataTable.columns.length; i++) {
        var dataTableColumn = dataTable.columns[i];
        var uiGridColumn = getDefaultColumnDef(dataTableColumn.id, dataTableColumn.name);
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

    var refreshDataTableGrid = function() {
      var dataTable = $scope.dataTable;
      delete $scope.dataTable;
      $timeout(function() {
        $scope.dataTable = dataTable;
      }, 0);
    };

    $scope.findByTestcaseId = function() {
      DataTable.get({ testcaseId: $stateParams.testcaseId }, function(dataTable) {
        updateDataTableGrid(dataTable);

        //  show the grid
        $scope.dataTable = dataTable;
      }, function(response) {
        IronTestUtils.openErrorHTTPResponseModal(response);
      });
    };

    $scope.addColumn = function(columnType) {
      DataTable.addColumn({ testcaseId: $stateParams.testcaseId, columnType: columnType }, {}, function(dataTable) {
        updateDataTableGrid(dataTable, true);
      }, function(response) {
        IronTestUtils.openErrorHTTPResponseModal(response);
      });
    };

    $scope.afterColumnNameEdit = function(col) {
      var colDef = col.colDef;
      delete colDef.headerCellTemplate;
      var oldName = colDef.name;
      var newName = col.name;

      if (newName !== oldName) {
        DataTable.renameColumn({ testcaseId: $stateParams.testcaseId, columnId: colDef.dataTableColumnId, newName: newName }, {},
        function() {
          var newColDef = getDefaultColumnDef(colDef.dataTableColumnId, newName);
          Object.assign(colDef, newColDef);
          refreshDataTableGrid();
        }, function(response) {
          IronTestUtils.openErrorHTTPResponseModal(response);
        });
      } else {
        refreshDataTableGrid();
      }
    };

    $scope.addRow = function() {
      DataTable.addRow({ testcaseId: $stateParams.testcaseId }, {}, function(dataTable) {
        updateDataTableGrid(dataTable);
      }, function(response) {
        IronTestUtils.openErrorHTTPResponseModal(response);
      });
    };
  }
]);