'use strict';

//  NOTICE:
//    The $scope here prototypically inherits from the $scope of TestcasesController,
angular.module('irontest').controller('DataTableController', ['$scope', 'IronTestUtils', '$stateParams', 'DataTable',
    '$timeout', '$uibModal',
  function($scope, IronTestUtils, $stateParams, DataTable, $timeout, $uibModal) {
    var stringCellUpdate = function(dataTableColumnId, rowIndex, newValue) {
      DataTable.updateStringCellValue({
        testcaseId: $stateParams.testcaseId,
        columnId: dataTableColumnId,
        rowIndex: rowIndex
      }, {
        value: newValue
      }, function() {
        $scope.$emit('successfullySaved');
      }, function(response) {
        IronTestUtils.openErrorHTTPResponseModal(response);
      });
    };

    var getRowIndexByRowEntity = function(rowEntity) {
      return $scope.dataTableGridOptions.data.map(function(e) { return e.$$hashKey; }).indexOf(rowEntity.$$hashKey);
    };

    $scope.dataTableGridOptions = {
      enableSorting: false,
      onRegisterApi: function(gridApi) {
        gridApi.edit.on.afterCellEdit($scope, function(rowEntity, colDef, newValue, oldValue){
          if (newValue !== oldValue) {
            var rowIndex = getRowIndexByRowEntity(rowEntity);
            stringCellUpdate(colDef.dataTableColumnId, rowIndex, newValue);
          }
        });
      }
    };

    var getDefaultColumnDef = function(dataTableColumnId, columnName, dataTableColumnType) {
      return {
        dataTableColumnId: dataTableColumnId,    //  not standard ui grid property for column def
        dataTableColumnType: dataTableColumnType,    //  not standard ui grid property for column def
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
        var uiGridColumn = getDefaultColumnDef(dataTableColumn.id, dataTableColumn.name, dataTableColumn.type);
        if (lastColumnHeaderInEditMode === true && i === dataTable.columns.length - 1) {
          uiGridColumn.headerCellTemplate = 'dataTableGridEditableHeaderCellTemplate.html';
        }
        if (dataTableColumn.type === 'String') {    //  it is a string column
          uiGridColumn.enableCellEdit = true;
          uiGridColumn.enableCellEditOnFocus = true;
          uiGridColumn.editableCellTemplate = 'dataTableGridStringEditableCellTemplate.html';
        } else {                                    //  it is an endpoint column
          uiGridColumn.enableCellEdit = false;
          uiGridColumn.cellTemplate = 'dataTableGridEndpointCellTemplate.html';
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

    $scope.afterColumnNameEdit = function(col, event) {
      if (event) {
        if (event.keyCode === 13 || event.keyCode === 27) {
          event.preventDefault();
        } else {                     // keys typed other than Enter and ESC do not trigger anything
          return;
        }
      }

      var colDef = col.colDef;
      delete colDef.headerCellTemplate;
      var oldName = colDef.name;
      var newName = col.name;

      if (newName !== oldName) {
        DataTable.renameColumn({ testcaseId: $stateParams.testcaseId, columnId: colDef.dataTableColumnId, newName: newName }, {},
        function() {
          var newColDef = getDefaultColumnDef(colDef.dataTableColumnId, newName, colDef.dataTableColumnType);
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

    $scope.stringCellDblClicked = function(rowEntity, col) {
      var columnName = col.name;
      var oldValue = rowEntity[columnName];

      //  open modal dialog
      var modalInstance = $uibModal.open({
        templateUrl: '/ui/views/testcases/datatable-string-cell-textarea-editor-modal.html',
        controller: 'DataTableStringCellTextareaEditorModalController',
        size: 'lg',
        windowClass: 'datatable-string-cell-textarea-editor-modal',
        resolve: {
          rowEntity: function() {
            return rowEntity;
          },
          columnName: function() {
            return columnName;
          }
        }
      });

      //  handle result from modal dialog
      modalInstance.result.then(function closed() {}, function dismissed() {
        if (rowEntity[columnName] !== oldValue) {
          var rowIndex = getRowIndexByRowEntity(rowEntity);
          stringCellUpdate(col.colDef.dataTableColumnId, rowIndex, rowEntity[columnName]); //  save immediately (no timeout)
        }
      });
    };

    $scope.selectManagedEndpoint = function(rowEntity, col) {
      var colDef = col.colDef;
      var endpointType = colDef.dataTableColumnType.replace('Endpoint', '');

      //  open modal dialog
      var modalInstance = $uibModal.open({
        templateUrl: '/ui/views/endpoints/list-modal.html',
        controller: 'SelectManagedEndpointModalController',
        size: 'lg',
        windowClass: 'select-managed-endpoint-modal',
        resolve: {
          endpointType: function() {
            return endpointType;
          },
          titleSuffix: function() {
            return 'for [' + rowEntity.Caption + '] > ' + col.name;
          }
        }
      });

      //  handle result from modal dialog
      modalInstance.result.then(function closed(selectedEndpoint) {
        console.log(selectedEndpoint);

        var rowIndex = getRowIndexByRowEntity(rowEntity);
        DataTable.updateEndpointCellValue({
          testcaseId: $stateParams.testcaseId,
          columnId: colDef.dataTableColumnId,
          rowIndex: rowIndex,
          newEndpointId: selectedEndpoint.id
        }, {
        }, function() {
          rowEntity[col.name] = selectedEndpoint;
          $scope.$emit('successfullySaved');
        }, function(response) {
          IronTestUtils.openErrorHTTPResponseModal(response);
        });
      }, function dismissed() {
        //  Modal dismissed. Do nothing.
      });
    };
  }
]);