'use strict';

angular.module('iron-test').controller('EnvEntriesController', ['$scope', 'EnvEntries', 'Environments', 'PageNavigation', '$location', '$stateParams', '$state', 'uiGridConstants',
  function($scope, EnvEntries, Environments, PageNavigation, $location, $stateParams, $state, uiGridConstants) {
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
          },
          "required": ["name"]
        },
        intface: {
          type: "object",
          properties: {
            name: { type: "string" }
          },
          "required": ["name"]
        },
        endpoint: {
          type: "object",
          properties: {
            name: { type: "string" }
          },
          "required": ["name"]
        }
      },
      "required": ["id", "enventryId", "intfaceId", "endpointId"]
    };

    $scope.form = {
      environmentname: [
        {
          key: "environment.name",
          notitle: true,
          htmlClass: 'spacer-bottom-0',
          readonly: true
        }
      ],
      environmentdesc: [
        {
          key: "environment.description",
          notitle: true,
          htmlClass: 'spacer-bottom-0',
          type: "textarea",
          readonly: true
        }
      ],
      interfacename: [
        {
          key: "intface.name",
          notitle: true,
          htmlClass: 'spacer-bottom-0',
          readonly: true
        }
      ],
      interfacedesc: [
        {
          key: "intface.description",
          notitle: true,
          htmlClass: 'spacer-bottom-0',
          type: "textarea",
          readonly: true
        }
      ],
      endpointname: [
        {
          key: "endpoint.name",
          notitle: true,
          htmlClass: 'spacer-bottom-0',
          readonly: true
        }
      ],
      endpointdesc: [
        {
          key: "endpoint.description",
          notitle: true,
          htmlClass: 'spacer-bottom-0',
          type: "textarea",
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

    $scope.findOne = function() {
      // entry returned from other pages
      var model = PageNavigation.returns.pop();
      if (model) {
        $scope.enventry = model;
      } else {
        if ($stateParams.enventryId) {
          // edit an existing entry
          EnvEntries.get({
            enventryId: $stateParams.enventryId
          }, function(enventry) {
            $scope.enventry = enventry;
          });
        } else if ($stateParams.environmentId) {
          // create a new enventry
          $scope.enventry.environmentId = Number($stateParams.environmentId);

          Environments.get({
            environmentId: $scope.enventry.environmentId
          }, function(environment) {
            $scope.enventry.environment = environment;
          });
        }
      }
    };
  }
]);
