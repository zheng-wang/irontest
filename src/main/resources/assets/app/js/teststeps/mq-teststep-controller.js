'use strict';

//  NOTICE:
//    The $scope here prototypically inherits from the $scope of teststeps-controller.js.
//    ng-include also creates a scope.
angular.module('iron-test').controller('MQTeststepController', ['$scope', 'Testruns', 'IronTestUtils', '$timeout',
  function($scope, Testruns, IronTestUtils, $timeout) {
    var timer;
    $scope.testrun = {};

    $scope.actionChanged = function(isValid) {
      //  clear previous action status
      $scope.actionStatus = null;
      $scope.testrun.response = null;
      $scope.teststep.assertions = [];

      //  setup new action assertion
      if ($scope.teststep.otherProperties.action === 'CheckDepth') {
        $scope.teststep.assertions[0] = {
          name: 'Queue depth equals',
          type: 'IntegerEquals',
          otherProperties: {
            number: 0
          }
        };
      }

      $scope.autoSave(isValid);
    };

    $scope.doAction = function() {
      if (timer) $timeout.cancel(timer);
      $scope.actionStatus = 'ongoing';

      var testrun = {
        teststep: $scope.teststep
      };
      var testrunRes = new Testruns(testrun);
      testrunRes.$save(function(response) {
        $scope.testrun.response = response.response;
        $scope.actionStatus = 'finished';
        $timeout(function() {
          $scope.actionStatus = null;
        }, 15000);
      }, function(response) {
        $scope.actionStatus = 'failed';
        IronTestUtils.openErrorHTTPResponseModal(response);
      });
    };
  }
]);
