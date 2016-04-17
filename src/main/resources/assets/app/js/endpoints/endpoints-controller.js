'use strict';

angular.module('iron-test').controller('EndpointsController', ['$scope', 'Endpoints', '$stateParams', '$state',
    'uiGridConstants', '$timeout',
  function($scope, Endpoints, $stateParams, $state, uiGridConstants, $timeout) {
    var timer;
    //  use object instead of primitives, so that child scope can update the values
    $scope.savingStatus = {
      saveSuccessful: null,
      savingErrorMessage: null
    };

    $scope.autoSave = function(isValid) {
      if (timer) $timeout.cancel(timer);
      timer = $timeout(function() {
        $scope.update(isValid);
      }, 2000);
    };

    $scope.update = function(isValid) {
      if (isValid) {
        $scope.endpoint.$update(function(response) {
          $scope.savingStatus.saveSuccessful = true;
          $scope.endpoint = response;
        }, function(error) {
          $scope.savingStatus.savingErrorMessage = error.data.message;
          $scope.savingStatus.saveSuccessful = false;
        });
      } else {
        $scope.savingStatus.submitted = true;
      }
    };

    $scope.findOne = function() {
      Endpoints.get({
        environmentId: $stateParams.environmentId,
        endpointId: $stateParams.endpointId
      }, function(endpoint) {
        $scope.endpoint = endpoint;
      }, function(error) {
        alert('Error');
      });
    };
  }
]);
