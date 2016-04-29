'use strict';

angular.module('iron-test').controller('EndpointsController', ['$scope', 'Endpoints', '$stateParams', '$state',
    'uiGridConstants', '$timeout', 'IronTestUtils',
  function($scope, Endpoints, $stateParams, $state, uiGridConstants, $timeout, IronTestUtils) {
    var timer;
    //  use object instead of primitives, so that child scope can update the values
    $scope.savingStatus = {
      saveSuccessful: null,
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
        }, function(response) {
          IronTestUtils.openErrorHTTPResponseModal(response);
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
      }, function(response) {
        IronTestUtils.openErrorHTTPResponseModal(response);
      });
    };
  }
]);
