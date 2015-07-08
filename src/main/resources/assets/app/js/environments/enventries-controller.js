'use strict';

angular.module('service-testing-tool').controller('EnvEntriesController', ['$scope', 'EnvEntries', '$stateParams', '$state', 'uiGridConstants',
  function($scope, EnvEntries, $stateParams, $state, uiGridConstants) {
     $scope.schema = {
      type: "object",
      properties: {
        id: { type: "integer" },
        environmentId: { type: "integer" },
        intfaceId: { type: "integer" },
        endpointId: { type: "integer" }
      },
      "required": ["id", "enventryId", "intfaceId", "endpointId"]
    };

    $scope.form = [
      {
        key: "environmentId",
        title: "Environment",
        readonly: true
      },
      {
        key: "intfaceId",
        title: "Interface"
      },
      {
        key: "endpointId",
        title: "Endpoint"
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
          $state.go('enventry_all');
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
      }
    };
  }
]);
