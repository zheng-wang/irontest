'use strict';

//  NOTICE:
//    The $scope here prototypically inherits from the $scope of TeststepsController.
//    ng-include also creates a scope.
angular.module('irontest').controller('IIBTeststepController', ['$scope', 'Teststeps', 'IronTestUtils', '$timeout',
  function($scope, Teststeps, IronTestUtils, $timeout) {
    var timer;
    $scope.steprun = {};

    $scope.endpointTypeChanged = function(isValid) {
      $scope.teststep.endpoint.otherProperties = null;

      if (!$scope.isInShareEndpointMode()) {
        //  save test step immediately (so as to update endpoint)
        $scope.update(isValid);
      }
    };

    var clearPreviousRunStatus = function() {
      if (timer) $timeout.cancel(timer);
      $scope.steprun = {};
    };

    $scope.actionChanged = function(isValid) {
      clearPreviousRunStatus();

      //  save test step immediately
      $scope.update(isValid);
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
