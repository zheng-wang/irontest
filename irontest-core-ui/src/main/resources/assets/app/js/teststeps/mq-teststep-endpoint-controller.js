'use strict';

//  NOTICE:
//    The $scope here prototypically inherits from the $scope of TeststepsEndpointController.
//    ng-include also creates a scope.
angular.module('irontest').controller('MQTeststepEndpointController', ['$scope',
  function($scope) {
    $scope.connectionModeChanged = function(isValid) {
      var endpointProperties = $scope.teststep.endpoint.otherProperties;
      endpointProperties.host = null;
      endpointProperties.port = null;
      endpointProperties.svrConnChannelName = null;

      if (!$scope.isInShareEndpointMode()) {
        //  update test step immediately (no timeout)
        $scope.update(isValid);
      }
    };
  }
]);