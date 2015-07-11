'use strict';

angular.module('service-testing-tool').controller('EnvEntriesController', ['$scope', 'EnvEntries', 'Environments', 'Intfaces', 'Endpoints', 'PageNavigation', '$location', '$stateParams', '$state', 'uiGridConstants',
  function($scope, EnvEntries, Environments, Intfaces, Endpoints, PageNavigation, $location, $stateParams, $state, uiGridConstants) {
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

    $scope.form = {
      environment: [
        {
          key: "environment.name",
          title: "Environment",
          htmlClass: 'spacer-bottom-0',
          readonly: true
        },
        {
          key: "environment.description",
          notitle: true,
          readonly: true
        }
      ],
      interface: [
        {
          key: "intface.name",
          title: "Interface",
          htmlClass: 'spacer-bottom-0',
          readonly: true
        },
        {
          key: "intface.description",
          notitle: true,
          htmlClass: 'spacer-bottom-0',
          readonly: true
        }
      ],
      endpoint: [
        {
          key: "endpoint.name",
          title: "Endpoint",
          htmlClass: 'spacer-bottom-0',
          readonly: true
        },
        {
          key: "endpoint.description",
          notitle: true,
          htmlClass: 'spacer-bottom-0',
          readonly: true
        }
      ]
    };

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

    $scope.goto = function(state, params, expect) {
      var context = {
        model: $scope.enventry,
        url: $location.path(),
        expect: expect
      };

      PageNavigation.contexts.push(context);

      $state.go(state, params);
    };

    $scope.closeAlert = function(index) {
      $scope.alerts.splice(index, 1);
    };

    $scope.remove = function(enventry) {
      enventry.$remove(function(response) {
          $state.go('environment_edit', {environmentId: enventry.environmentId});
      });
    };

    var populateReturnObj = function() {
      // Return from the interface details page
      var returnObj = PageNavigation.returns.pop();

      if (returnObj) {
        if (returnObj.intfaceId) {
          $scope.enventry.intfaceId = returnObj.intfaceId;

          Intfaces.get({
            intfaceId: $scope.enventry.intfaceId
          }, function(intface) {
            $scope.enventry.intface = intface;
          });
        }
        if (returnObj.endpointId) {
          $scope.enventry.endpointId = returnObj.endpointId;

          Endpionts.get({
            endpointId: $scope.enventry.endpointId
          }, function(endpoint) {
            $scope.enventry.endpoint = endpoint;
          });
        }
      }
    };

    $scope.findOne = function() {
      if ($stateParams.enventryId) {
        EnvEntries.get({
          enventryId: $stateParams.enventryId
        }, function(enventry) {
          $scope.enventry = enventry;
          populateReturnObj();
        });
      // create a new enventry
      } else if ($stateParams.environmentId) {
        $scope.enventry.environmentId = Number($stateParams.environmentId);

        Environments.get({
          environmentId: $scope.enventry.environmentId
        }, function(environment) {
          $scope.enventry.environment = environment;
          populateReturnObj();
        });
      }
    };
  }
]);
