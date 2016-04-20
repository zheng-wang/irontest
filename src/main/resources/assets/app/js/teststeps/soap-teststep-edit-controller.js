'use strict';

//  NOTICE:
//    The $scope here prototypically inherits from the $scope of teststeps-controller.js.
//    ng-include also creates a scope.
angular.module('iron-test').controller('SOAPTeststepEditController', ['$scope', 'Testruns', '$state',
  function($scope, Testruns, $state) {
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
      }, function(error) {
        alert('Error');
      });
    };

    $scope.assertionsAreaLoadedCallback = function() {
      $scope.$broadcast('assertionsAreaLoaded');
    };
  }
]);
