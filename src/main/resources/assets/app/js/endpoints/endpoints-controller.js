'use strict';

angular.module('iron-test').controller('EndpointsController', ['$scope', 'Endpoints', 'PageNavigation', '$location', '$stateParams', '$state', 'uiGridConstants',
  function($scope, Endpoints, PageNavigation, $location, $stateParams, $state, uiGridConstants) {
    $scope.create_update = function(form) {
      $scope.$broadcast('schemaFormValidate');

      if (form.$valid) {
        if (this.endpoint.id) {
          var endpoint = this.endpoint;
          endpoint.$update(function() {
          }, function(exception) {
          });
        } else {
          var endpoint = new Endpoints(this.endpoint);
          endpoint.$save(function(response) {
            PageNavigation.contexts.push($scope.context);
            $state.go('endpoint_edit', {endpointId: response.id});
          }, function(exception) {
            $scope.alerts.push({type: 'warning', msg: exception.data});
          });
        }
      }
    };

    $scope.remove = function(endpoint) {
      endpoint.$remove(function(response) {
          $state.go('endpoint_all');
      });
    };

    $scope.find = function() {
      $scope.columnDefs = [
        {
          name: 'name', width: 200, minWidth: 100,
          sort: {
            direction: uiGridConstants.ASC,
            priority: 1
          },
          cellTemplate:'gridCellTemplate.html'
        },
        {
          name: 'description', width: 600, minWidth: 300
        },
        {
          name: 'handler', width: 200, minWidth: 100
        }
      ];

      Endpoints.query(function(endpoints) {
        $scope.endpoints = endpoints;
      });
    };

    $scope.return = function() {
      PageNavigation.returns.push($scope.context.model);
      $location.path($scope.context.url);
    };

    $scope.select = function() {
      $scope.context.model.endpointId = $scope.endpoint.id;

      Endpoints.get({
        endpointId: $scope.context.model.endpointId
      }, function(endpoint) {
        $scope.context.model.endpoint = endpoint;

        PageNavigation.returns.push($scope.context.model);
        $location.path($scope.context.url);
      });
    };

    $scope.findOne = function() {
      Endpoints.get({
        environmentId: $stateParams.environmentId,
        endpointId: $stateParams.endpointId
      }, function(endpoint) {
        $scope.endpoint = endpoint;
      }, function(error) {
        alert('Error');
      });
    };
  }
]);
