'use strict';

//  NOTICE:
//    The $scope here prototypically inherits from the $scope of TestcasesController.
angular.module('irontest').controller('UDPsController', ['$scope', 'UDPs', 'IronTestUtils', '$stateParams',
    '$uibModal',
  function($scope, UDPs, IronTestUtils, $stateParams, $uibModal) {
    //  user defined properties of the test case
    $scope.udps = [];

    var udpUpdate = function(udp) {
      udp.$update(function(response) {
        $scope.$emit('successfullySaved');
      }, function(response) {
        IronTestUtils.openErrorHTTPResponseModal(response);
      });
    };

    $scope.udpGridOptions = {
      data: 'udps', enableFiltering: true, enableColumnMenus: false,
      rowTemplate: '<div grid="grid" class="ui-grid-draggable-row" draggable="true"><div ng-repeat="(colRenderIndex, col) in colContainer.renderedColumns track by col.colDef.name" class="ui-grid-cell" ng-class="{ \'ui-grid-row-header-cell\': col.isRowHeader, \'custom\': true }" ui-grid-cell></div></div>',
      columnDefs: [
        {
          name: 'name', width: '30%', enableCellEdit: true, enableCellEditOnFocus: true,
          editableCellTemplate: 'udpGridNameEditableCellTemplate.html'
        },
        {
          name: 'value', enableCellEdit: true, enableCellEditOnFocus: true,
          editableCellTemplate: 'udpGridValueEditableCellTemplate.html'
        },
        {
          name: 'delete', width: 70, minWidth: 60, enableSorting: false, enableFiltering: false, enableCellEdit: false,
          cellTemplate: 'udpGridDeleteCellTemplate.html'
        }
      ],
      onRegisterApi: function(gridApi) {
        $scope.gridApi = gridApi;

        var idOfRowBeingEdited = null;
        gridApi.edit.on.beginCellEdit ($scope, function(rowEntity, colDef){
          idOfRowBeingEdited = rowEntity.id;
        });
        gridApi.edit.on.afterCellEdit($scope, function(rowEntity, colDef, newValue, oldValue){
          if (rowEntity.id === idOfRowBeingEdited && newValue !== oldValue) {  //  this edit is real text edit, not triggered by moving row (using the ui-grid-draggable-rows plugin)
            udpUpdate(rowEntity);
          }
        });
        gridApi.draggableRows.on.rowDropped($scope, function (info) {
          //  get dragged cell column index
          var draggedCellColumnName = gridApi.cellNav.getFocusedCell().col.colDef.name;
          var draggedCellColumnIndex;
          $scope.udpGridOptions.columnDefs.some(function(columnDef, index) {
            if (columnDef.name === draggedCellColumnName) {
              draggedCellColumnIndex = index;
              return true;
            } else {
              return false;
            }
          });

          //  Refocus edit to target cell. This is a hack as gridApi.grid.cellNav is non-public API.
          gridApi.grid.cellNav.clearFocus();
          gridApi.grid.cellNav.lastRowCol = null;
          info.targetRow.children[draggedCellColumnIndex].children[0].click();

          var toSequence;
          if (info.fromIndex > info.toIndex) {    // row moved up
            toSequence = $scope.udps[info.toIndex + 1].sequence;
          } else {                            // row moved down
            toSequence = $scope.udps[info.toIndex - 1].sequence;
          }

          UDPs.move({
            testcaseId: $scope.testcase.id,
            fromSequence: info.draggedRowEntity.sequence,
            toSequence: toSequence
          }, {}, function(response) {
            $scope.$emit('successfullySaved');
            $scope.udps = response;  // this is necessary as server side will change sequence values of udps (including the dragged udp and some not-dragged upds).
          }, function(response) {
            IronTestUtils.openErrorHTTPResponseModal(response);
          });
        });

        $scope.$parent.handleTestcaseRunResultOutlineAreaDisplay();
      }
    };

    $scope.$on('testcaseRunResultOutlineAreaShown', function() {
      if ($scope.gridApi) {
        $scope.gridApi.core.handleWindowResize();
      }
    });

    $scope.findByTestcaseId = function() {
      UDPs.query({ testcaseId: $stateParams.testcaseId }, function(returnUDPs) {
        $scope.udps = returnUDPs;
      }, function(response) {
        IronTestUtils.openErrorHTTPResponseModal(response);
      });
    };

    $scope.createUDP = function() {
      var udp = new UDPs();
      udp.$save({ testcaseId: $stateParams.testcaseId }, function(returnUDP) {
        $scope.udps.push(returnUDP);
      }, function(response) {
        IronTestUtils.openErrorHTTPResponseModal(response);
      });
    };

    $scope.removeUDP = function(udp) {
      udp.$remove(function(response) {
        IronTestUtils.deleteArrayElementByProperty($scope.udps, 'id', udp.id);
      }, function(response) {
        IronTestUtils.openErrorHTTPResponseModal(response);
      });
    };

    $scope.valueCellDblClicked = function(udp) {
      var oldValue = udp.value;

      //  open modal dialog
      var modalInstance = $uibModal.open({
        templateUrl: '/ui/views/testcases/udp-value-textarea-editor-modal.html',
        controller: 'UDPValueTextareaEditorModalController',
        size: 'lg',
        windowClass: 'udp-value-textarea-editor-modal',
        resolve: {
          udp: function() {
            return udp;
          }
        }
      });

      //  handle result from modal dialog
      modalInstance.result.then(function closed() {}, function dismissed() {
        if (udp.value !== oldValue) {
          udpUpdate(udp);            //  save immediately (no timeout)
        }
      });
    };
  }
]);