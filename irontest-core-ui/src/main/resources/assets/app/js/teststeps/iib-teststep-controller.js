'use strict';

//  NOTICE:
//    The $scope here prototypically inherits from the $scope of teststeps-controller.js.
//    ng-include also creates a scope.
angular.module('irontest').controller('IIBTeststepController', ['$scope', 'Testruns', 'IronTestUtils', '$timeout',
  function($scope, Testruns, IronTestUtils, $timeout) {
    var timer;
    $scope.testrun = {};

    var clearPreviousRunStatus = function() {
      if (timer) $timeout.cancel(timer);
      $scope.testrun = {};
    };

    $scope.actionChanged = function(isValid) {
      clearPreviousRunStatus();

      //  save test step
      $scope.update(isValid);
    };

    $scope.doAction = function() {
      clearPreviousRunStatus();

      var testrun = {
        teststep: $scope.teststep
      };
      var testrunRes = new Testruns(testrun);
      $scope.testrun.status = 'ongoing';
      testrunRes.$save(function(response) {
        $scope.testrun.status = 'finished';
        timer = $timeout(function() {
          $scope.testrun.status = null;
        }, 15000);
      }, function(response) {
        $scope.testrun.status = 'failed';
        IronTestUtils.openErrorHTTPResponseModal(response);
      });
    };
  }
]);
