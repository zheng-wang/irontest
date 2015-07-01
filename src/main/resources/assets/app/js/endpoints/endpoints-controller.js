'use strict';

angular.module('service-testing-tool').controller('EndpointsController', ['$scope', 'Endpoints', '$stateParams', '$state', 'uiGridConstants',
  function($scope, Endpoints, $stateParams, $state, uiGridConstants) {
    $scope.schema = {
      type: "object",
      properties: {
        id: { type: "integer" },
        name: { type: "string" },
        description: { type: "string" },
        host: { type: "string" },
        port: { type: "integer" },
        protocol: { type: "string" },
        ctxroot: { type: "string" }
      },
      "required": ["name", "description", "host", "port", "protocol"]
    };

    $scope.form = [
      {
        key: "name",
        title: "Name",
        condition: "! endpoint.id"
      },
      {
        key: "description",
        title: "Description",
        type: "textarea"
      },
      {
        key: "protocol",
        title: "Protocol",
        type: "select",
        titleMap: [
          { value: "http", name: "http" }
        ]
      },
      {
        key: "host",
        title: "Host"
      },
      {
        key: "port",
        title: "Port"
      },
      {
        key: "ctxroot",
        title: "Context Root"
      }
    ];

    $scope.endpoint = {};

    $scope.create_update = function(form) {
      $scope.$broadcast('schemaFormValidate');

      if (form.$valid) {
        if (this.endpoint.id) {
          var endpoint = this.endpoint;
          endpoint.$update(function() {
            $state.go('endpoint_edit', {endpointId: endpoint.id});
          });
        } else {
          var endpoint = new Endpoints(this.endpoint);
          endpoint.$save(function(response) {
            $state.go('endpoint_edit', {endpointId: response.id});
          });
        }
      }
    };

    $scope.stateGo = function(state) {
      $state.go(state);
    };

    $scope.remove = function(endpoint) {
      endpoint.$remove(function(response) {
          $state.go('endpoint_grid');
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
