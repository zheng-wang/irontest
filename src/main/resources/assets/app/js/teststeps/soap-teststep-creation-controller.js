'use strict';

angular.module('iron-test').controller('SOAPTeststepCreationController', ['$scope', 'Teststeps', '$stateParams',
    '$state', 'IronTestUtils',
  function($scope, Teststeps, $stateParams, $state, IronTestUtils) {
    $scope.create = function(isValid) {
      if (isValid) {
        var teststep = new Teststeps({
          testcaseId: $stateParams.testcaseId,
          name: this.name,
          description: this.description,
          type: 'SOAP' /*,
          properties: {
            wsdlUrl: this.wsdlUrl,
            wsdlBindingName: this.wsdlBinding.name,
            wsdlOperationName: this.wsdlOperation
          }*/
        });

        teststep.$save(function(response) {
          $state.go('teststep_edit', {testcaseId: response.testcaseId, teststepId: response.id});
        }, function(response) {
          IronTestUtils.openErrorHTTPResponseModal(response);
        });
      } else {
        $scope.submitted = true;
      }
    };
  }
]);
