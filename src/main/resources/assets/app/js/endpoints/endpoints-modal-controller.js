'use strict';

angular.module('iron-test').controller('EndpointsModalController', ['$scope', '$http',
    'uiGridConstants', '$uibModalInstance', 'endpointType',
  function($scope, $http, uiGridConstants, $uibModalInstance, endpointType) {
    $scope.endpointType = endpointType;

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

    $scope.find = function() {
      var url = 'api/jsonservice/findManagedEndpointsByType?type=' + $scope.endpointType;
      $http
        .get(url)
        .then(function successCallback(response) {
          $scope.endpoints = response.data;
        }, function errorCallback(response) {
          alert('Error');
        });
    };

    $scope.cancel = function () {
      $uibModalInstance.dismiss('cancel');
    };

    $scope.select = function(endpoint) {
      $uibModalInstance.close(endpoint);
    };
  }
]);
