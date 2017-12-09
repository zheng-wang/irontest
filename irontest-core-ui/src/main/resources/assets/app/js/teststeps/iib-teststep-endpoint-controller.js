'use strict';

//  NOTICE:
//    The $scope here prototypically inherits from the $scope of TeststepsEndpointController.
//    ng-include also creates a scope.
angular.module('irontest').controller('IIBTeststepEndpointController', ['$scope',
  function($scope) {
    $scope.endpointTypeChanged = function(isValid) {
      $scope.teststep.endpoint.otherProperties = {};

      if (!$scope.isInShareEndpointMode()) {
        //  save test step immediately (so as to update endpoint)
        $scope.update(isValid);
      }
    };
  }
]);
