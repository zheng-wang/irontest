'use strict';

//  NOTICE:
//    The $scope here prototypically inherits from the $scope of TeststepsEndpointController.
//    ng-include also creates a scope.
angular.module('irontest').controller('IIBTeststepEndpointController', ['$scope',
  function($scope) {
    $scope.endpointTypeChanged = function(isValid) {
      var endpoint = $scope.teststep.endpoint;
      endpoint.otherProperties = {};
      endpoint.otherProperties['@type'] = endpoint.type === 'MQ' ? 'MQEndpointProperties' : 'IIBEndpointProperties';

      if (!$scope.isInShareEndpointMode()) {
        //  save test step immediately (so as to update endpoint)
        $scope.update(isValid);
      }
    };
  }
]);
