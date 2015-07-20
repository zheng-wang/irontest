'use strict';

angular.module('service-testing-tool').controller('AssertionsController', ['$scope', 'Assertions',
  '$stateParams', 'uiGridConstants',
  function($scope, Assertions, $stateParams, uiGridConstants) {
    //  use this to avoid conflict with parent scope
    $scope.assertionsModelObj = {};

    $scope.assertionsModelObj.gridOptions = {
      columnDefs: [
        {
          name: 'name', width: 250, minWidth: 250,
          sort: {
            direction: uiGridConstants.ASC,
            priority: 1
          },
          cellTemplate: 'assertionGridNameCellTemplate.html'
        },
        {name: 'type', width: 100, minWidth: 100},
        {name: 'delete', width: 100, minWidth: 100, enableSorting: false,
          cellTemplate: 'assertionGridDeleteCellTemplate.html'
        }
      ],
      onRegisterApi: function (gridApi) {
        $scope.assertionsModelObj.gridApi = gridApi;
      }
    };

    $scope.assertionsModelObj.findAll = function() {
      Assertions.query(
        {
          testcaseId: $stateParams.testcaseId,
          teststepId: $stateParams.teststepId
        }, function(response) {
          $scope.assertionsModelObj.gridOptions.data = response;
        }, function(error) {
          alert('Error');
        });
    };


  }
]);
