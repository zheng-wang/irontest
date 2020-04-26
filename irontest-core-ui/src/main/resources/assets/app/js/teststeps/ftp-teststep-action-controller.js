'use strict';

//  NOTICE:
//    The $scope here prototypically inherits from the $scope of TeststepsActionController.
//    ng-include also creates a scope.
angular.module('irontest').controller('FTPTeststepActionController', ['$scope', 'IronTestUtils', '$timeout',
    'Teststeps',
  function($scope, IronTestUtils, $timeout, Teststeps) {
    var timer;
    $scope.steprun = {};

    $scope.textMessageTabs = {
      activeIndex: 0
    }

    var clearPreviousRunStatus = function() {
      if (timer) $timeout.cancel(timer);
      $scope.steprun = {};
    };

    $scope.endpointInfoIncomplete = function() {
      var endpointOtherProperties = $scope.teststep.endpoint.otherProperties;
      return !endpointOtherProperties.host || !endpointOtherProperties.port;
    };

    $scope.actionInfoIncomplete = function() {
      var apiRequest = $scope.teststep.apiRequest;
      return apiRequest.fileFrom === 'Text' && (!apiRequest.fileContent || !apiRequest.targetFilePath);
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
      }, function(error) {
        $scope.steprun.status = 'failed';
        IronTestUtils.openErrorHTTPResponseModal(error);
      });
    };
  }
]);
