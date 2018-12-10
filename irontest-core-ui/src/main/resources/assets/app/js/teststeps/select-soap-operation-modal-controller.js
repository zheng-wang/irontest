'use strict';

angular.module('irontest').controller('SelectSOAPOperationModalController', ['$scope', '$uibModalInstance',
    '$http', 'IronTestUtils', 'wsdlURL',
  function($scope, $uibModalInstance, $http, IronTestUtils, wsdlURL) {
    $scope.wsdlURL = wsdlURL;

    $scope.loadWSDLBindings = function() {
      $http
        .get('api/wsdls/' + encodeURIComponent($scope.wsdlURL) + '/bindings')
        .then(function successCallback(response) {
          $scope.wsdlBindings = response.data;
          $scope.wsdlBinding = $scope.wsdlBindings[0];
          $scope.refreshOperations();
        }, function errorCallback(response) {
          IronTestUtils.openErrorHTTPResponseModal(response);
        });
    };

    $scope.refreshOperations = function() {
      $scope.wsdlOperations = $scope.wsdlBinding.operations;
      $scope.wsdlOperation = $scope.wsdlOperations[0];
    };

    $scope.cancel = function () {
      $uibModalInstance.dismiss('cancel');
    };

    $scope.ok = function() {
      $http
        .get('api/wsdls/' + encodeURIComponent($scope.wsdlURL) + '/bindings/' + $scope.wsdlBinding.name +
          '/operations/' + $scope.wsdlOperation)
        .then(function successCallback(operationInfo) {
          $uibModalInstance.close(operationInfo.data);
        }, function errorCallback(error) {
          IronTestUtils.openErrorHTTPResponseModal(error);
        });
    };
  }
]);
