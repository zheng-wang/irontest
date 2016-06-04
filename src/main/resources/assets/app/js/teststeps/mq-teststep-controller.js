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
      $scope.testrun = {};
      $scope.teststep.assertions = [];

      //  setup new action assertion
      if ($scope.teststep.otherProperties.action === 'CheckDepth') {
        $scope.teststep.assertions[0] = {
          name: 'MQ queue depth equals',
          type: 'IntegerEqual',
          otherProperties: {
            number: 0
          }
        };
      } else if ($scope.teststep.otherProperties.action === 'Dequeue') {
        $scope.teststep.assertions[0] = {
          name: 'Dequeue XML equals',
          type: 'XMLEqual',
          otherProperties: {
            expectedXML: null
          }
        };
      }

      //  save test step
      $scope.autoSave(isValid);
    };

    $scope.doAction = function() {
      if (timer) $timeout.cancel(timer);

      var testrun = {
        teststep: $scope.teststep
      };
      var testrunRes = new Testruns(testrun);
      $scope.testrun.status = 'ongoing';
      testrunRes.$save(function(response) {
        $scope.testrun.response = response.response;
        $scope.testrun.status = 'finished';
        $timeout(function() {
          $scope.testrun.status = null;
        }, 15000);
      }, function(response) {
        $scope.testrun.status = 'failed';
        IronTestUtils.openErrorHTTPResponseModal(response);
      });
    };
  }
]);
