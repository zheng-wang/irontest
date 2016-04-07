'use strict';

angular.module('iron-test').controller('TeststepsController', ['$scope', 'Teststeps', '$stateParams',
  function($scope, Teststeps, $stateParams) {
    $scope.stepType = $stateParams.stepType;
    $scope.teststep = {};

    $scope.findOne = function() {
      Teststeps.get({
        testcaseId: $stateParams.testcaseId,
        teststepId: $stateParams.teststepId
      }, function (response) {
        $scope.teststep = response;
      });
    };
  }
]);
