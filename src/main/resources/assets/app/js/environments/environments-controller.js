'use strict';

angular.module('iron-test').controller('EnvironmentsController', ['$scope', 'Environments',
    'PageNavigation', '$location', '$stateParams', '$state', 'uiGridConstants', '$timeout', 'Endpoints',
  function($scope, Environments, PageNavigation, $location, $stateParams, $state, uiGridConstants, $timeout, Endpoints) {

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

    $scope.update = function(isValid) {
      if (isValid) {
        $scope.environment.$update(function(response) {
          $scope.saveSuccessful = true;
          $scope.environment = response;
        }, function(error) {
          $scope.savingErrorMessage = error.data.message;
          $scope.saveSuccessful = false;
        });
      } else {
        $scope.submitted = true;
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

    $scope.findOne = function() {
      Environments.get({
        environmentId: $stateParams.environmentId
      }, function(environment) {
        $scope.environment = environment;
      }, function(error) {
        alert('Error');
      });
    };

    $scope.removeEndpoint = function(endpoint) {
      var endpointService = new Endpoints(endpoint);
      endpointService.$remove(function(response) {
        $state.go($state.current, {}, {reload: true});
      }, function(error) {
        alert('Error');
      });
    };
  }
]);
