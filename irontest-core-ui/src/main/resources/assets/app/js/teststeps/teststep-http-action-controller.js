'use strict';

//  This controller is shared between SOAP test step and HTTP test step.
//  NOTICE:
//    The $scope here prototypically inherits from the $scope of TeststepsActionController.
//    ng-include also creates a scope.
angular.module('irontest').controller('TeststepHTTPActionController', ['$scope', '$rootScope', 'Teststeps',
    'IronTestUtils', '$uibModal', 'uiGridConstants', '$timeout', '$window',
  function($scope, $rootScope, Teststeps, IronTestUtils, $uibModal, uiGridConstants, $timeout, $window) {
    const HTTP_HEADER_GRID_NAME_COLUMN_WIDTH = '30%';
    switch ($scope.teststep.type) {
      case 'SOAP':
        $scope.showHTTPHeaders = ($window.localStorage.showHTTPHeadersOnSOAPTeststepEditView === 'true');
        break;
      case 'HTTP':
        $scope.showHTTPHeaders = ($window.localStorage.showHTTPHeadersOnHTTPTeststepEditView === 'true');
        break;
    }

    var clearRunStatus = function() {
      $scope.steprun = {};
    };

    clearRunStatus();

    $scope.showRequestBodyArea = function() {
      var teststep = $scope.teststep;
      return teststep.type === 'SOAP' || (teststep.type === 'HTTP' && (!teststep.otherProperties.httpMethod ||
        teststep.otherProperties.httpMethod === 'POST' || teststep.otherProperties.httpMethod === 'PUT'));
    };

    var createHTTPHeader = function(gridMenuEvent) {
      $scope.teststep.otherProperties.httpHeaders.push(
        { name: 'name1', value: 'value1' }
      );
      $scope.update(true, function selectTheNewRow() {
        var headers = $scope.teststep.otherProperties.httpHeaders;
        $scope.requestHTTPHeaderGridApi.grid.modifyRows(headers);
        $scope.requestHTTPHeaderGridApi.selection.selectRow(headers[headers.length - 1]);
      });
    };

    var deleteHTTPHeader = function(gridMenuEvent) {
      var selectedRow = $scope.requestHTTPHeaderGridApi.selection.getSelectedRows()[0];
      var httpHeaders = $scope.teststep.otherProperties.httpHeaders;
      IronTestUtils.deleteArrayElementByProperty(httpHeaders, '$$hashKey', selectedRow.$$hashKey);
      $scope.update(true);
    };

    $scope.$watch('teststep.otherProperties.httpHeaders', function() {
      $scope.requestHTTPHeaderGridOptions.data = $scope.teststep.otherProperties.httpHeaders;
    });

    $scope.requestHTTPHeaderGridOptions = {
      enableSorting: false, enableRowHeaderSelection: false, multiSelect: false,
      enableGridMenu: true, enableColumnMenus: false, gridMenuShowHideColumns: false,
      rowHeight: 20, enableHorizontalScrollbar: uiGridConstants.scrollbars.NEVER,
      columnDefs: [
        { name: 'name', width: HTTP_HEADER_GRID_NAME_COLUMN_WIDTH,
          headerTooltip: 'Double click to edit', enableCellEdit: true,
          editableCellTemplate: 'httpHeaderGridEditableCellTemplate.html' },
        { name: 'value', headerTooltip: 'Double click to edit', enableCellEdit: true, cellTooltip: true,
          editableCellTemplate: 'httpHeaderGridEditableCellTemplate.html' }
      ],
      gridMenuCustomItems: [
        { title: 'Create', order: 210, action: createHTTPHeader,
          shown: function() {
            return !$rootScope.appStatus.isForbidden();
          }
        },
        { title: 'Delete', order: 220, action: deleteHTTPHeader,
          shown: function() {
            return !$rootScope.appStatus.isForbidden() &&
              $scope.requestHTTPHeaderGridApi.selection.getSelectedRows().length === 1;
          }
        }
      ],
      onRegisterApi: function (gridApi) {
        $scope.requestHTTPHeaderGridApi = gridApi;
        gridApi.edit.on.afterCellEdit($scope, function(rowEntity, colDef, newValue, oldValue){
          if (newValue !== oldValue) {
            $scope.update(true);
          }
        });
      }
    };

    $scope.$watch('steprun.response.httpHeaders', function() {
      if ($scope.steprun.response) {
        $scope.responseHTTPHeaderGridOptions.data = $scope.steprun.response.httpHeaders;
      }
    });

    $scope.responseHTTPHeaderGridOptions = {
      enableSorting: false, enableColumnMenus: false,
      rowHeight: 20, enableHorizontalScrollbar: uiGridConstants.scrollbars.NEVER,
      columnDefs: [
        { name: 'name', width: HTTP_HEADER_GRID_NAME_COLUMN_WIDTH },
        { name: 'value', cellTooltip: true }
      ],
      onRegisterApi: function (gridApi) {
        $scope.httpHeadersAreaLoadedCallback();
      }
    };

    $scope.toggleHTTPHeadersArea = function() {
      var httpHeadersArea = document.getElementById('httpHeadersArea');
      if (httpHeadersArea) {
        var elementHeight = httpHeadersArea.offsetHeight;
        $scope.$broadcast('elementRemovedFromColumn', { elementHeight: elementHeight });
      }
      $scope.showHTTPHeaders = !$scope.showHTTPHeaders;

      switch ($scope.teststep.type) {
        case 'SOAP':
          $window.localStorage.showHTTPHeadersOnSOAPTeststepEditView = $scope.showHTTPHeaders;
          break;
        case 'HTTP':
          $window.localStorage.showHTTPHeadersOnHTTPTeststepEditView = $scope.showHTTPHeaders;
          break;
      }
    };

    $scope.httpHeadersAreaLoadedCallback = function() {
      $timeout(function() {
        var elementHeight = document.getElementById('httpHeadersArea').offsetHeight;
        //  the heightAdjustableElementInColumn (request response area) is always loaded before the httpHeadersArea is
        //  loaded, no matter on Invocation tab newly loaded, or on httpHeadersArea toggled on.
        $scope.$broadcast('elementInsertedIntoColumn', { elementHeight: elementHeight });
      });
    };

    $scope.$on('InvocationTabDeselected', function(event, args) {
      //  there is elementInsertedIntoColumn event for the http headers area, so there must be elementRemovedFromColumn
      //  event for it as well, just like the assertions area.
      //  this is to avoid the request/response area height being cut again and again when switching between the
      //  Invocation tab and other tab.
      var httpHeadersArea = document.getElementById('httpHeadersArea');
      if (httpHeadersArea) {
        $scope.$broadcast('elementRemovedFromColumn', { elementHeight: httpHeadersArea.offsetHeight });
      }
    });

    $scope.generateSOAPRequest = function() {
      //  open modal dialog
      var modalInstance = $uibModal.open({
        templateUrl: '/ui/views/teststeps/soap/select-soap-operation-modal.html',
        controller: 'SelectSOAPOperationModalController',
        size: 'lg',
        windowClass: 'select-soap-operation-modal',
        resolve: {
          wsdlURL: function () {
            return $scope.teststep.endpoint.otherProperties.wsdlURL;
          }
        }
      });

      //  handle result from modal dialog
      modalInstance.result.then(function closed(operationInfo) {
        //  save the generated request to teststep (in parent scope/controller)
        $scope.teststep.request = operationInfo.sampleRequest;
        $scope.update(true);  //  save immediately (no timeout)
      }, function dismissed() {
        //  Modal dismissed. Do nothing.
      });
    };

    $scope.invoke = function() {
      clearRunStatus();

      var teststep = new Teststeps($scope.teststep);
      $scope.steprun.status = 'ongoing';
      teststep.$run(function(basicTeststepRun) {
        $scope.steprun.status = 'finished';
        $scope.steprun.response = basicTeststepRun.response;
      }, function(error) {
        $scope.steprun.status = 'failed';
        IronTestUtils.openErrorHTTPResponseModal(error);
      });
    };
  }
]);
