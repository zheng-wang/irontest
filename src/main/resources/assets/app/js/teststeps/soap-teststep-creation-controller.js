'use strict';

angular.module('iron-test').controller('SOAPTeststepCreationController', ['$scope', 'Teststeps', '$stateParams',
    '$state', '$http', '_',
  function($scope, Teststeps, $stateParams, $state, $http, _) {
    $scope.loadWsdl = function() {
      $http
        .get('api/wsdls/anywsdl/operations', {
          params: {
            wsdlUrl: $scope.wsdlUrl
          }
        })
        .success(function(data, status) {
          $scope.wsdlBindings = data;
          $scope.wsdlBinding = $scope.wsdlBindings[0];
          $scope.wsdlOperations = $scope.wsdlBindings[0].operations;
          $scope.wsdlOperation = $scope.wsdlOperations[0];
        })
        .error(function(data, status) {
          alert('Error');
        });
    };

    $scope.refreshOperations = function() {
      $scope.wsdlOperations = _.findWhere(
        $scope.wsdlBindings, { name: $scope.wsdlBinding.name }).operations;
      $scope.wsdlOperation = $scope.wsdlOperations[0];
    };

    $scope.create = function(isValid) {
      if (isValid) {
        var teststep = new Teststeps({
          testcaseId: $stateParams.testcaseId,
          name: this.name,
          description: this.description,
          type: 'SOAP',
          properties: {
            wsdlUrl: this.wsdlUrl,
            wsdlBindingName: this.wsdlBinding.name,
            wsdlOperationName: this.wsdlOperation
          }
        });

        teststep.$save(function(response) {
          $state.go('teststep_edit', {testcaseId: response.testcaseId, teststepId: response.id});
        }, function(error) {
          alert('Error');
        });
      } else {
        $scope.submitted = true;
      }
    };
  }
]);
