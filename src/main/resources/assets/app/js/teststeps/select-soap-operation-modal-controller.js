'use strict';

angular.module('iron-test').controller('SelectSOAPOperationModalController', ['$scope', '$uibModalInstance',
    '$http', '_', 'IronTestUtils', 'soapAddress',
  function($scope, $uibModalInstance, $http, _, IronTestUtils, soapAddress) {
    $scope.soapAddress = soapAddress;
    $scope.wsdlUrl = soapAddress + '?wsdl';

    $scope.loadWsdl = function() {
      $http
        .get('api/wsdls/anywsdl/operations', {
          params: {
            wsdlUrl: $scope.wsdlUrl
          }
        })
        .then(function successCallback(response) {
          $scope.wsdlBindings = response.data;
          $scope.wsdlBinding = $scope.wsdlBindings[0];
          $scope.wsdlOperations = $scope.wsdlBindings[0].operations;
          $scope.wsdlOperation = $scope.wsdlOperations[0];
        }, function errorCallback(response) {
          IronTestUtils.openErrorHTTPResponseModal(response);
        });
    };

    $scope.refreshOperations = function() {
      $scope.wsdlOperations = _.findWhere(
        $scope.wsdlBindings, { name: $scope.wsdlBinding.name }).operations;
      $scope.wsdlOperation = $scope.wsdlOperations[0];
    };

    $scope.cancel = function () {
      $uibModalInstance.dismiss('cancel');
    };

    $scope.select = function(endpoint) {
      $uibModalInstance.close(endpoint);
    };
  }
]);
