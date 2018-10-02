'use strict';

//  NOTICE:
//    The $scope here prototypically inherits from the $scope of TeststepsEndpointController.
//    ng-include also creates a scope.
angular.module('irontest').controller('SOAPTeststepEndpointController', ['$scope', '$rootScope',
  function($scope, $rootScope) {
    $scope.toggleWSDLURLByConvention = function(isValid) {
      var endpoint = $scope.teststep.endpoint;
      var endpointProperties = endpoint.otherProperties;
      if (endpointProperties.wsdlURLByConvention === true) {
        endpointProperties.wsdlURL = (endpoint.url === null ? '' : endpoint.url) + '?wsdl';
      }

      if (!$scope.isInShareEndpointMode()) {
        $scope.update(isValid);    //  save immediately (no timeout)
      }
    };

    $rootScope.$on('endpointSOAPAddressChanged', function(event, args) {
      $scope.endpointSOAPAddressChanged(args.isValid);
    });

    $scope.endpointSOAPAddressChanged = function(isValid) {
      var endpoint = $scope.teststep.endpoint;
      var endpointProperties = endpoint.otherProperties;
      if (endpointProperties.wsdlURLByConvention === true) {
        endpointProperties.wsdlURL = endpoint.url + '?wsdl';
      }

      if (!$scope.isInShareEndpointMode()) {
        $scope.autoSave(isValid);
      }
    };
  }
]);
