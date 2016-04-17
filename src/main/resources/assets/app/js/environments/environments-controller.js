'use strict';

angular.module('iron-test').controller('EnvironmentsController', ['$scope', 'Environments',
    'PageNavigation', '$location', '$stateParams', '$state', 'uiGridConstants',
  function($scope, Environments, PageNavigation, $location, $stateParams, $state, uiGridConstants) {

    $scope.saveSuccessful = null;
    var timer;
    $scope.autoSave = function(isValid) {
      if (timer) $timeout.cancel(timer);
      timer = $timeout(function() {
        $scope.update(isValid);
      }, 2000);
    };

    $scope.envGridColumnDefs = [
      {
        name: 'name', width: 200, minWidth: 100,
        sort: {
          direction: uiGridConstants.ASC,
          priority: 1
        },
        cellTemplate:'envGridNameCellTemplate.html'
      },
      {
        name: 'description', width: 600, minWidth: 300
      },
      {
        name: 'delete', width: 100, minWidth: 80, enableSorting: false, enableFiltering: false,
        cellTemplate: 'envGridDeleteCellTemplate.html'
      }
    ];

    $scope.endpointGridColumnDefs = [
      {
        field: 'name', width: 200, minWidth: 100,
        sort: {
          direction: uiGridConstants.ASC,
          priority: 1
        },
        cellTemplate:'endpointGridNameCellTemplate.html'
      },
      {name: 'type', width: 80, minWidth: 80},
      {name: 'description', width: 500, minWidth: 300},
      {
        name: 'delete', width: 100, minWidth: 80, enableSorting: false, enableFiltering: false,
        cellTemplate: 'endpointGridDeleteCellTemplate.html'
      }
    ];

    $scope.create = function(isValid) {
      if (isValid) {
        var environment = new Environments({
          name: this.name,
          description: this.description
        });
        environment.$save(function(response) {
          $state.go('environment_edit', {environmentId: response.id});
        }, function(error) {
          alert('Error');
        });
      } else {
        $scope.submitted = true;
      }
    };

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

    $scope.remove = function(environment) {
      environment.$remove(function(response) {
        $state.go($state.current, {}, {reload: true});
      }, function(error) {
        alert('Error');
      });
    };

    $scope.find = function() {
      Environments.query(function(environments) {
        $scope.environments = environments;
      }, function(error) {
        alert('Error');
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

    $scope.findOne = function() {
      Environments.get({
        environmentId: $stateParams.environmentId
      }, function(environment) {
        $scope.environment = environment;
      }, function(error) {
        alert('Error');
      });
    };
  }
]);
