'use strict';

//  NOTICE:
//    The $scope here prototypically inherits from the $scope of teststeps-controller.js.
//    ng-include also creates a scope.
angular.module('iron-test').controller('IIBTeststepController', ['$scope', 'Testruns', 'IronTestUtils', '$timeout',
  function($scope, Testruns, IronTestUtils, $timeout) {
    var timer;

    $scope.doAction = function() {
      if (timer) $timeout.cancel(timer);
      $scope.action = $scope.teststep.otherProperties.action;
      $scope.actionStatus = 'ongoing';

      var testrun = {
        teststep: $scope.teststep
      };
      var testrunRes = new Testruns(testrun);
      testrunRes.$save(function(response) {
        console.log(response);
        $scope.actionStatus = 'finished';
        $timeout(function() {
          $scope.actionStatus = null;
        }, 10000);
      }, function(response) {
        $scope.actionStatus = 'failed';
        IronTestUtils.openErrorHTTPResponseModal(response);
      });
    };
  }
]);
