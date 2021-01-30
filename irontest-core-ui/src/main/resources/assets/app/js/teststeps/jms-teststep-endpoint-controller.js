'use strict';

//  NOTICE:
//    The $scope here prototypically inherits from the $scope of TeststepsEndpointController.
//    ng-include also creates a scope.
angular.module('irontest').controller('JMSTeststepEndpointController', ['$scope',
  function($scope) {
    $scope.jmsProviderChanged = function(isValid) {
      var otherProperties = $scope.teststep.endpoint.otherProperties;
      if (otherProperties.jmsProvider === 'Solace') {
        otherProperties['@type'] = 'JMSSolaceEndpointProperties';
      }

      if (!$scope.isInShareEndpointMode()) {
        $scope.update(isValid);  //  update immediately (no timeout)
      }
    };
  }
]);
