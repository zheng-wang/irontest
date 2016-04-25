'use strict';

//  NOTICE:
//    The $scope here prototypically inherits from the $scope of teststeps-controller.js.
//    ng-include also creates a scope.
angular.module('iron-test').controller('SOAPTeststepEditController', ['$scope', 'Testruns', 'IronTestUtils',
  function($scope, Testruns, IronTestUtils) {
    $scope.tempData = {};
    $scope.showAssertionsArea = false;

    $scope.invoke = function() {
      var testrun = {
        teststepId: $scope.teststep.id,
        request: $scope.teststep.request
      };
      var testrunRes = new Testruns(testrun);
      testrunRes.$save(function(response) {
        $scope.tempData.soapResponse = response.response;
      }, function(response) {
        IronTestUtils.openErrorMessageModal(response);
      });
    };

    $scope.assertionsAreaLoadedCallback = function() {
      $scope.$broadcast('assertionsAreaLoaded');
    };
  }
]);
