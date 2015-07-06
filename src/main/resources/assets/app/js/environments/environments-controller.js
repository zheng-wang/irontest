'use strict';

angular.module('service-testing-tool').controller('EnvironmentsController', ['$scope', 'Environments', '$stateParams', '$state', 'uiGridConstants',
  function($scope, Environments, $stateParams, $state, uiGridConstants) {
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
        title: "Name",
        validationMessage: "The Name is required and should be less than 50 characters"
      },
      {
        key: "description",
        title: "Description",
        type: "textarea",
        validationMessage: "The Description is required and should be less than 500 characters"
      }
    ];

    $scope.environment = {};

    $scope.alerts = [];

    $scope.create_update = function(form) {
      $scope.$broadcast('schemaFormValidate');

      if (form.$valid) {
        if (this.environment.id) {
          var environment = this.environment;
          environment.$update(function() {
            $scope.alerts.push({type: 'success', msg: 'The Environment has been updated successfully'});
          }, function(exception) {
            $scope.alerts.push({type: 'warning', msg: exception.data});
          });
        } else {
          var environment = new Environments(this.environment);
          environment.$save(function(response) {
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
        }
      ];

      Environments.query(function(environments) {
        $scope.environments = environments;
      });
    };

    $scope.findOne = function() {
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
