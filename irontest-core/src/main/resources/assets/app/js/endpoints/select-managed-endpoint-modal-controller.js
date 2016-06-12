'use strict';

angular.module('irontest').controller('SelectManagedEndpointModalController', ['$scope', 'uiGridConstants', '$uibModalInstance',
    'endpoints',
  function($scope, uiGridConstants, $uibModalInstance, endpoints) {
    $scope.endpoints = endpoints;

    $scope.endpointModalGridColumnDefs = [
      {
        name: 'environment.name', displayName: 'Environment', width: 130, minWidth: 100,
        sort: {
          direction: uiGridConstants.ASC,
          priority: 1
        }
      },
      {
        name: 'name', width: 300, minWidth: 100,
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
