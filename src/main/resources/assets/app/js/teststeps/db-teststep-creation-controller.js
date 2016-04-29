'use strict';

angular.module('iron-test').controller('DBTeststepCreationController', ['$scope', 'Teststeps', '$state', '$stateParams',
    'IronTestUtils',
  function($scope, Teststeps, $state, $stateParams, IronTestUtils) {
    $scope.create = function(isValid) {
      if (isValid) {
        var teststep = new Teststeps({
          testcaseId: $stateParams.testcaseId,
          name: this.name,
          description: this.description,
          type: 'DB'
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
