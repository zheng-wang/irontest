'use strict';

//  NOTICE:
//    The $scope here prototypically inherits from the $scope of TeststepsActionController.
//    ng-include also creates a scope.
angular.module('irontest').controller('JMSTeststepActionController', ['$scope', '$timeout',
  function($scope, $timeout) {
    var timer;
    $scope.steprun = {};

    var clearPreviousRunStatus = function() {
      if (timer) $timeout.cancel(timer);
      $scope.steprun = {};
    };

    $scope.destinationTypeChanged = function(isValid) {
      var teststep = $scope.teststep;
      if (teststep.otherProperties.destinationType === 'Topic') {
        teststep.action = 'Publish';
      } else {              //  destinationType is Queue
        teststep.action = null;
      }
      $scope.actionChanged(isValid);
    };

    $scope.actionChanged = function(isValid) {
      clearPreviousRunStatus();

      //  update test step immediately (no timeout)
      $scope.update(isValid);
    };
  }
]);
