'use strict';

angular.module('service-testing-tool').controller('EnvEntriesController', ['$scope', 'EnvEntries', 'Environments', 'Intfaces', 'Endpoints', '$stateParams', '$state', 'uiGridConstants',
  function($scope, EnvEntries, Environments, Intfaces, Endpoints, $stateParams, $state, uiGridConstants) {
     $scope.schema = {
      type: "object",
      properties: {
        id: { type: "integer" },
        environmentId: { type: "integer" },
        intfaceId: { type: "integer" },
        endpointId: { type: "integer" },
        environment: {
          type: "object",
          properties: {
            name: { type: "string" }
          }
        },
        intface: {
          type: "object",
          properties: {
            name: { type: "string" }
          }
        },
        endpoint: {
          type: "object",
          properties: {
            name: { type: "string" }
          }
        }
      },
      "required": ["id", "enventryId", "intfaceId", "endpointId"]
    };

    $scope.form = [
      {
          key: "environment.name",
          title: "Environment",
          readonly: true
      },
      {
        key: "environment.description",
        notitle: true,
        readonly: true
      },
      {
        key: "intface.name",
        title: "Interface",
        readonly: true
      },
      {
        key: "intface.description",
        notitle: true,
        readonly: true
      },
      {
        key: "endpoint.name",
        title: "Endpoint",
        readonly: true
      },
      {
        key: "endpoint.description",
        notitle: true,
        readonly: true
      }
    ];

    $scope.enventry = {};

    $scope.alerts = [];

    $scope.create_update = function(form) {
      $scope.$broadcast('schemaFormValidate');

      if (form.$valid) {
        if (this.enventry.id) {
          var enventry = this.enventry;
          enventry.$update(function() {
            $scope.alerts.push({type: 'success', msg: 'The Environment Entry has been updated successfully'});
          }, function(exception) {
            $scope.alerts.push({type: 'warning', msg: exception.data});
          });
        } else {
          var enventry = new EnvEntries(this.enventry);
          enventry.$save(function(response) {
            $state.go('enventry_edit', {enventryId: response.id});
          }, function(exception) {
            $scope.alerts.push({type: 'warning', msg: exception.data});
          });
        }
      }
    };

    $scope.closeAlert = function(index) {
      $scope.alerts.splice(index, 1);
    };

    $scope.remove = function(enventry) {
      enventry.$remove(function(response) {
          $state.go('environment_edit', {environmentId: enventry.environmentId});
      });
    };

    $scope.findOne = function() {
      if ($stateParams.enventryId) {
        EnvEntries.get({
          enventryId: $stateParams.enventryId
        }, function(enventry) {
          $scope.enventry = enventry;
        });
      } else if ($stateParams.environmentId) {
        $scope.enventry.environmentId = Number($stateParams.environmentId);

        Environments.get({
          environmentId: $scope.enventry.environmentId
        }, function(environment) {
          $scope.enventry.environment = environment;
        });
      }
    };
  }
]);
