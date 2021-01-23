'use strict';

//  NOTICE:
//    The $scope here prototypically inherits from the $scope of TeststepsActionController.
//    ng-include also creates a scope.
angular.module('irontest').controller('IIBTeststepActionController', ['$scope', 'Teststeps', 'IronTestUtils', '$timeout',
  function($scope, Teststeps, IronTestUtils, $timeout) {
    var timer;
    $scope.steprun = {};

    var clearPreviousRunStatus = function() {
      if (timer) $timeout.cancel(timer);
      $scope.steprun = {};
    };

    $scope.actionChanged = function(isValid) {
      clearPreviousRunStatus();

      //  save test step immediately
      $scope.update(isValid);
    };

    $scope.endpointInfoIncomplete = function() {
      var endpoint = $scope.teststep.endpoint;
      var endpointOtherProperties = endpoint.otherProperties;
      if (endpoint.type === 'MQ') {
        return !endpointOtherProperties.queueManagerName ||
          (endpointOtherProperties.connectionMode === 'Client' && (!endpointOtherProperties.host ||
          !endpointOtherProperties.port || !endpointOtherProperties.svrConnChannelName));
      } else {        //  endpoint type is IIB
        return !endpoint.host || !endpoint.port;
      }
    };

    $scope.actionInfoIncomplete = function() {
      var teststep = $scope.teststep;
      return !teststep.action || !teststep.otherProperties.integrationServerName ||
        !teststep.otherProperties.messageFlowName;
    };

    $scope.doAction = function() {
      clearPreviousRunStatus();

      var teststep = new Teststeps($scope.teststep);
      $scope.steprun.status = 'ongoing';
      teststep.$run(function(basicTeststepRun) {
        $scope.steprun.status = 'finished';
        timer = $timeout(function() {
          $scope.steprun.status = null;
        }, 15000);
        $scope.steprun.infoMessage = basicTeststepRun.infoMessage;
      }, function(error) {
        $scope.steprun.status = 'failed';
        IronTestUtils.openErrorHTTPResponseModal(error);
      });
    };
  }
]);
