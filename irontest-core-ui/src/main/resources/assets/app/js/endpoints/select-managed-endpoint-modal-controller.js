'use strict';

angular.module('irontest').controller('SelectManagedEndpointModalController', ['$scope', 'uiGridConstants',
    '$uibModalInstance', 'endpointType', 'ManagedEndpoints', 'titleSuffix',
  function($scope, uiGridConstants, $uibModalInstance, endpointType, ManagedEndpoints, titleSuffix) {
    $scope.endpointType = endpointType;
    $scope.titleSuffix = titleSuffix;

    //  find managed endpoints by type
    ManagedEndpoints.query({ type: endpointType },
      function successCallback(response) {
        $scope.endpoints = response;
      }, function errorCallback(response) {
        IronTestUtils.openErrorHTTPResponseModal(response);
      });

    $scope.endpointModalGridColumnDefs = [
      {
        name: 'environment.name', displayName: 'Environment', width: '30%', minWidth: 100,
        sort: {
          direction: uiGridConstants.ASC,
          priority: 1
        }
      },
      {
        name: 'name', cellTemplate: 'endpointModalGridNameCellTemplate.html'
      }
    ];

    $scope.cancel = function () {
      $uibModalInstance.dismiss('cancel');
    };

    $scope.select = function(endpoint) {
      $uibModalInstance.close(endpoint);
    };
  }
]);
