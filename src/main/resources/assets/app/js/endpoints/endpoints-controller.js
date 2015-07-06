'use strict';

angular.module('service-testing-tool').controller('EndpointsController', ['$scope', 'Endpoints', '$stateParams', '$state', 'uiGridConstants',
  function($scope, Endpoints, $stateParams, $state, uiGridConstants) {
    $scope.schema = {
      type: "object",
      properties: {
        id: { type: "integer" },
        name: { type: "string", maxLength: 50 },
        description: { type: "string", maxLength: 500 },
        url: {
          type: "string",
          maxLength: 200,
          pattern: "^http:\/{2}(([a-zA-Z0-9]|[a-zA-Z0-9][a-zA-Z0-9\-]*[a-zA-Z0-9])\.)*([A-Za-z0-9]|[A-Za-z0-9][A-Za-z0-9\-]*[A-Za-z0-9])(\/([a-z0-9_\.-])+)*\/?[A-Za-z0-9]|[A-Za-z0-9][A-Za-z0-9\-\.]*[A-Za-z0-9]$"
        },
        username: {
          type: "string",
          maxLength: 20,

        },
        password: {
          type: "string",
          maxLength: 20
        }
      },
      "required": ["name", "description", "url"]
    };

    $scope.form = [
      {
        key: "name",
        title: "Name",
        validationMessage: "The Name is required and should be less than 50 characters"
      },
      {
        key: "description",
        title: "Description",
        type: "textarea",
        validationMessage: "The Description is required and should be less than 500 characters"
      },
      {
        key: "url",
        title: "URL",
        validationMessage: "The URL is required and should be started with http"
      },
      {
        key: "username",
        title: "User Name"
      },
      {
        key: "password",
        title: "Password",
        type: "password"
      }
    ];

    $scope.endpoint = {};

    $scope.alerts = [];

    $scope.create_update = function(form) {
      $scope.$broadcast('schemaFormValidate');

      if (form.$valid) {
        if (this.endpoint.id) {
          var endpoint = this.endpoint;
          endpoint.$update(function() {
            $scope.alerts.push({type: 'success', msg: 'The Endpoint has been updated successfully'});
          }, function(exception) {
            $scope.alerts.push({type: 'warning', msg: exception.data});
          });
        } else {
          var endpoint = new Endpoints(this.endpoint);
          endpoint.$save(function(response) {
            $state.go('endpoint_edit', {endpointId: response.id});
          }, function(exception) {
            $scope.alerts.push({type: 'warning', msg: exception.data});
          });
        }
      }
    };

    $scope.closeAlert = function(index) {
      $scope.alerts.splice(index, 1);
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
          name: 'address', width: 600, minWidth: 300
        }
      ];

      Endpoints.query(function(endpoints) {
        $scope.endpoints = endpoints;
      });
    };

    $scope.findOne = function() {
      if ($stateParams.endpointId) {
        Endpoints.get({
          endpointId: $stateParams.endpointId
        }, function(endpoint) {
          $scope.endpoint = endpoint;
        });
      }
    };
  }
]);
