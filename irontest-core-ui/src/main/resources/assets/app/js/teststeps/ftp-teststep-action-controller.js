'use strict';

//  NOTICE:
//    The $scope here prototypically inherits from the $scope of TeststepsActionController.
//    ng-include also creates a scope.
angular.module('irontest').controller('FTPTeststepActionController', ['$scope', 'IronTestUtils', '$timeout',
    'Teststeps', 'Upload',
  function($scope, IronTestUtils, $timeout, Teststeps, Upload) {
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
      return apiRequest.fileFrom === 'Text' && (!apiRequest.fileContent || !apiRequest.remoteFilePath);
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

    $scope.uploadAPIRequestFile = function(file) {
      if (file) {
        var url = 'api/testcases/' + $scope.teststep.testcaseId + '/teststeps/' + $scope.teststep.id + '/apiRequestFile';
        Upload.upload({
          url: url,
          data: {file: file}
        }).then(function successCallback(response) {
          $scope.$emit('successfullySaved');
          $scope.setTeststep(new Teststeps(response.data));
        }, function errorCallback(response) {
          IronTestUtils.openErrorHTTPResponseModal(response);
        });
      }
    };
  }
]);
