'use strict';

angular.module('irontest').controller('EnvironmentsController', ['$scope', 'Environments',
    '$stateParams', '$state', 'uiGridConstants', '$timeout', 'Endpoints', 'IronTestUtils',
  function($scope, Environments, $stateParams, $state, uiGridConstants, $timeout, Endpoints, IronTestUtils) {

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
        field: 'name', width: 350, minWidth: 100,
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

    $scope.create = function() {
      var environment = new Environments();
      environment.$save(function(response) {
        $state.go('environment_edit', {environmentId: response.id, newlyCreated: true});
      }, function(response) {
        IronTestUtils.openErrorHTTPResponseModal(response);
      });
    };

    $scope.update = function(isValid) {
      if (isValid) {
        $scope.environment.$update(function(response) {
          $scope.$broadcast('successfullySaved');
          $scope.environment = response;
        }, function(response) {
          IronTestUtils.openErrorHTTPResponseModal(response);
        });
      } else {
        $scope.submitted = true;
      }
    };

    $scope.remove = function(environment) {
      environment.$remove(function(response) {
        $state.go($state.current, {}, {reload: true});
      }, function(response) {
        IronTestUtils.openErrorHTTPResponseModal(response);
      });
    };

    $scope.find = function() {
      Environments.query(function(environments) {
        $scope.environments = environments;
      }, function(response) {
        IronTestUtils.openErrorHTTPResponseModal(response);
      });
    };

    $scope.environmentNewlyCreated = function() {
      return $stateParams.newlyCreated === true;
    };

    $scope.findOne = function() {
      $scope.activeTabIndex = $scope.environmentNewlyCreated() ? 0 : 1;
      Environments.get({
        environmentId: $stateParams.environmentId
      }, function(environment) {
        $scope.environment = environment;
      }, function(response) {
        IronTestUtils.openErrorHTTPResponseModal(response);
      });
    };

    $scope.createEndpoint = function(type) {
      var endpoint = new Endpoints({
        environment: { id: $stateParams.environmentId },
        type: type,
        otherProperties: null  //  this is to avoid Jackson 'Missing property' error (http://stackoverflow.com/questions/28089484/deserialization-with-jsonsubtypes-for-no-value-missing-property-error)
      });
      endpoint.$save(function(returnEndpoint) {
        $state.go('endpoint_edit', {environmentId: $stateParams.environmentId, endpointId: returnEndpoint.id,
          newlyCreated: true});
      }, function(response) {
        IronTestUtils.openErrorHTTPResponseModal(response);
      });
    };

    $scope.removeEndpoint = function(endpoint) {
      var endpointService = new Endpoints(endpoint);
      endpointService.$remove(function(response) {
        $state.go($state.current, {}, {reload: true});
      }, function(response) {
        IronTestUtils.openErrorHTTPResponseModal(response);
      });
    };
  }
]);
