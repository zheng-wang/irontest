'use strict';

//  This controller is for managed endpoints.
angular.module('irontest').controller('EndpointsController', ['$scope', 'Endpoints', '$stateParams', '$state',
    'uiGridConstants', '$timeout', 'IronTestUtils',
  function($scope, Endpoints, $stateParams, $state, uiGridConstants, $timeout, IronTestUtils) {
    $scope.endpointNewlyCreated = function() {
      return $stateParams.newlyCreated === true;
    };

    var timer;
    $scope.autoSave = function(isValid) {
      if (timer) $timeout.cancel(timer);
      timer = $timeout(function() {
        $scope.update(isValid);
      }, 2000);
    };

    $scope.update = function(isValid) {
      if (isValid) {
        $scope.endpoint.$update(function(response) {
          $scope.$broadcast('successfullySaved');
          $scope.endpoint = response;
        }, function(response) {
          IronTestUtils.openErrorHTTPResponseModal(response);
        });
      } else {
        $scope.submitted = true;
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
