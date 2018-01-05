'use strict';

//  NOTICE:
//    The $scope here prototypically inherits from the $scope of TestcasesController,
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
        gridApi.edit.on.afterCellEdit($scope, function(rowEntity, colDef, newValue, oldValue){
          if (newValue !== oldValue) {
            udpUpdate(rowEntity);
          }
        });
        gridApi.draggableRows.on.rowDropped($scope, function (info, dropTarget) {
          console.log(info);
          console.log(dropTarget);
        });
      }
    };

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
