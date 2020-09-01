'use strict';

//  For HTTP stub grid on test case edit view.
//  NOTICE:
//    The $scope here prototypically inherits from the $scope of TestcasesController.
angular.module('irontest').controller('HTTPStubsController', ['$scope', 'HTTPStubs', 'IronTestUtils', '$stateParams',
    '$state', '$timeout',
  function($scope, HTTPStubs, IronTestUtils, $stateParams, $state, $timeout) {
    $scope.httpStubs = [];

    $scope.httpStubGridOptions = {
      data: 'httpStubs', enableColumnMenus: false,
      rowTemplate: '<div grid="grid" class="ui-grid-draggable-row" draggable="true"><div ng-repeat="(colRenderIndex, col) in colContainer.renderedColumns track by col.colDef.name" class="ui-grid-cell" ng-class="{ \'ui-grid-row-header-cell\': col.isRowHeader, \'custom\': true }" ui-grid-cell></div></div>',
      columnDefs: [
        {
          name: 'number', displayName: 'NO.', width: 55, minWidth: 55,
          cellTemplate: 'httpStubGridNOCellTemplate.html'
        },
        {
          name: 'url', displayName: 'URL', width: '50%', cellTemplate:'httpStubGridURLCellTemplate.html'
        },
        {
          name: 'spec.request.method', displayName: 'Method', width: 70, minWidth: 70
        },
        {
          name: 'delete', width: 70, minWidth: 70, enableSorting: false,
          cellTemplate: 'httpStubGridDeleteCellTemplate.html'
        }
      ],
      onRegisterApi: function(gridApi) {
        $scope.gridApi = gridApi;

        gridApi.draggableRows.on.rowDropped($scope, function (info) {
          var toNumber;
          if (info.fromIndex > info.toIndex) {    // row moved up
            toNumber = $scope.httpStubs[info.toIndex + 1].number;
          } else {                            // row moved down
            toNumber = $scope.httpStubs[info.toIndex - 1].number;
          }

          HTTPStubs.move({
            testcaseId: $scope.testcase.id,
            fromNumber: info.draggedRowEntity.number,
            toNumber: toNumber
          }, {}, function(response) {
            $scope.$emit('successfullySaved');
            $scope.httpStubs = response;  // this is necessary as server side will change number values of http stubs.
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
      HTTPStubs.query({ testcaseId: $stateParams.testcaseId }, function(httpStubs) {
        $scope.httpStubs = httpStubs;
      }, function(response) {
        IronTestUtils.openErrorHTTPResponseModal(response);
      });
    };

    $scope.createHTTPStub = function() {
      var httpStub = new HTTPStubs();
      httpStub.$save({ testcaseId: $stateParams.testcaseId }, function(httpStub) {
        $state.go('httpstub_edit', {testcaseId: $stateParams.testcaseId, httpStubId: httpStub.id});
      }, function(response) {
        IronTestUtils.openErrorHTTPResponseModal(response);
      });
    };

    $scope.removeHTTPStub = function(httpStub) {
      httpStub.$remove(function(response) {
        IronTestUtils.deleteArrayElementByProperty($scope.httpStubs, 'id', httpStub.id);
      }, function(response) {
        IronTestUtils.openErrorHTTPResponseModal(response);
      });
    };

    $scope.loadAllHTTPStubs = function() {
      $scope.stubsLoadingStatus = null;
      HTTPStubs.loadAll({
        testcaseId: $stateParams.testcaseId
      }, {}, function successCallback(response) {
        $scope.stubsLoadingStatus = 'finished';
        $timeout(function() {
          $scope.stubsLoadingStatus = null;
        }, 10000);
      }, function errorCallback(response) {
        $scope.stubsLoadingStatus = null;
        IronTestUtils.openErrorHTTPResponseModal(response);
      });
    };
  }
]);