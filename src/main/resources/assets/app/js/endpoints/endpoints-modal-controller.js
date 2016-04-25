'use strict';

angular.module('iron-test').controller('EndpointsModalController', ['$scope', 'uiGridConstants', '$uibModalInstance',
    'endpoints',
  function($scope, uiGridConstants, $uibModalInstance, endpoints) {
    $scope.endpoints = endpoints;

    $scope.endpointModalGridColumnDefs = [
      {
        name: 'environmentName', displayName: 'Environment', width: 130, minWidth: 100,
        sort: {
          direction: uiGridConstants.ASC,
          priority: 1
        }
      },
      {
        name: 'name', width: 250, minWidth: 100,
        cellTemplate: 'endpointModalGridNameCellTemplate.html'
      },
      {
        name: 'description', width: 400, minWidth: 200
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
