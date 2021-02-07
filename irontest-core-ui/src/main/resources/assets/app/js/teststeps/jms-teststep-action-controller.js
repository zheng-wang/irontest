'use strict';

//  NOTICE:
//    The $scope here prototypically inherits from the $scope of TeststepsActionController.
//    ng-include also creates a scope.
angular.module('irontest').controller('JMSTeststepActionController', ['$scope', '$timeout', 'IronTestUtils', 'Teststeps',
  function($scope, $timeout, IronTestUtils, Teststeps) {
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

    $scope.endpointInfoIncomplete = function() {
      var endpoint = $scope.teststep.endpoint;
      var endpointOtherProperties = endpoint.otherProperties;
      return !endpointOtherProperties.jmsProvider ||
        (endpointOtherProperties.jmsProvider === 'Solace' && (!endpoint.host || !endpoint.port ||
          !endpointOtherProperties.vpn));
    };

    $scope.actionInfoIncomplete = function() {
      var teststep = $scope.teststep;
      if (!teststep.action) {
        return true;
      } else if (teststep.otherProperties.destinationType === 'Queue') {
        return !teststep.otherProperties.queueName;
      } else if (teststep.otherProperties.destinationType === 'Topic') {
        return !teststep.otherProperties.topicString;
      } else {
        return true;
      }
    };

    $scope.doAction = function() {
      clearPreviousRunStatus();

      var teststep = new Teststeps($scope.teststep);
      $scope.steprun.status = 'ongoing';
      teststep.$run(function(basicTeststepRun) {
        $scope.steprun.response = basicTeststepRun.response;
        $scope.steprun.status = 'finished';
        timer = $timeout(function() {
          $scope.steprun.status = null;
        }, 15000);
      }, function(error) {
        $scope.steprun.status = 'failed';
        IronTestUtils.openErrorHTTPResponseModal(error);
      });
    };
  }
]);
