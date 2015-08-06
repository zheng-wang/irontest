'use strict';

angular.module('service-testing-tool').controller('EnvironmentsController', ['$scope', 'Environments', 'EnvEntries', 'PageNavigation', '$location', '$stateParams', '$state', 'uiGridConstants',
  function($scope, Environments, EnvEntries, PageNavigation, $location, $stateParams, $state, uiGridConstants) {
    $scope.schema = {
      type: "object",
      properties: {
        id: { type: "integer" },
        name: { type: "string", maxLength: 50 },
        description: { type: "string", maxLength: 500 }
      },
      "required": ["name", "description"]
    };

    $scope.form = [
      {
        key: "name",
        validationMessage: "The Name is required and should be less than 50 characters"
      },
      {
        key: "description",
        type: "textarea",
        validationMessage: "The Description is required and should be less than 500 characters"
      }
    ];

    $scope.environment = {};

    $scope.alerts = [];

    $scope.create_update = function(form) {
      $scope.$broadcast('schemaFormValidate');

      if (form.$valid) {
        if ($scope.environment.id) {
          $scope.environment.$update(function(response) {
            $scope.environment = response;
            $scope.alerts.push({type: 'success', msg: 'The Environment has been updated successfully'});
          }, function(exception) {
            $scope.alerts.push({type: 'warning', msg: exception.data});
          });
        } else {
          var environment = new Environments($scope.environment);
          environment.$save(function(response) {
            PageNavigation.contexts.push($scope.context);
            $state.go('environment_edit', {environmentId: response.id});
          }, function(exception) {
            $scope.alerts.push({type: 'warning', msg: exception.data});
          });
        }
      }
    };

    $scope.closeAlert = function(index) {
      $scope.alerts.splice(index, 1);
    };

    $scope.remove = function(environment) {
      environment.$remove(function(response) {
          $state.go('environment_all');
      });
    };

    $scope.find = function() {
      $scope.envGridColumnDefs = [
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
        }
      ];

      Environments.query(function(environments) {
        $scope.environments = environments;
      });
    };

    $scope.goto = function(state, params, expect) {
      var context = {
        model: $scope.environment,
        url: $location.path(),
        expect: expect
      };

      PageNavigation.contexts.push(context);

      $state.go(state, params);
    };

    $scope.return = function() {
      PageNavigation.returns.push($scope.context.model);
      $location.path($scope.context.url);
    };

    $scope.select = function() {
      $scope.context.model.environmentId = $scope.environment.id;

      Environments.get({
        environmentId: $scope.context.model.environmentId
      }, function(environment) {
        $scope.context.model.environment = environment;

        PageNavigation.returns.push($scope.context.model);
        $location.path($scope.context.url);
      });
    };

    $scope.findOne = function() {
      $scope.context = PageNavigation.contexts.pop();

      $scope.enventryGridColumnDefs = [
        {
          field: 'intface.name', displayName: 'Interface', width: 200, minWidth: 100,
          sort: {
            direction: uiGridConstants.ASC,
            priority: 1
          },
          cellTemplate:'gridCellTemplate.html'
        },
        {
          field: 'endpoint.name', displayName: 'Endpoint',width: 600, minWidth: 300
        }
      ];

      if ($stateParams.environmentId) {
        Environments.get({
          environmentId: $stateParams.environmentId
        }, function(environment) {
          $scope.environment = environment;
        });
      }
    };
  }
]);
