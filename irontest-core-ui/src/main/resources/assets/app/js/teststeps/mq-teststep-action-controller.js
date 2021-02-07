'use strict';

//  NOTICE:
//    The $scope here prototypically inherits from the $scope of TeststepsActionController.
//    ng-include also creates a scope.
angular.module('irontest').controller('MQTeststepActionController', ['$scope', 'IronTestUtils', '$timeout', 'Upload',
    '$window', 'Teststeps',
  function($scope, IronTestUtils, $timeout, Upload, $window, Teststeps) {
    var timer;
    $scope.steprun = {};

    $scope.textMessageTabs = {
      activeIndex: 0
    }

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
      return !endpointOtherProperties.queueManagerName ||
        (endpointOtherProperties.connectionMode === 'Client' && (!endpoint.host || !endpoint.port ||
          !endpointOtherProperties.svrConnChannelName));
    };

    $scope.actionInfoIncomplete = function() {
      var teststep = $scope.teststep;
      if (!teststep.action) {
        return true;
      } else if (teststep.otherProperties.destinationType === 'Queue') {
        return !teststep.otherProperties.queueName || (
          teststep.action === 'Enqueue' && (
            teststep.requestType === 'Text' && !teststep.request ||
            teststep.requestType === 'File' && !teststep.requestFilename
          )
        );
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

    $scope.$watch('teststep.otherProperties', function() {
      if ($scope.teststep.otherProperties.rfh2Header) {
        $scope.includeRfh2Header = true;
      } else {
        $scope.includeRfh2Header = false;
      }
    });

    $scope.toggleRFH2Header = function(isValid) {
      var header = $scope.teststep.otherProperties.rfh2Header;
      if (header) {
        $scope.teststep.otherProperties.rfh2Header = null;
        $scope.update(isValid);
      } else {
        $scope.teststep.otherProperties.rfh2Header = { folders: [] };
        $scope.addRFH2Folder(isValid);
      }
    };

    $scope.addRFH2Folder = function(isValid) {
      var folders = $scope.teststep.otherProperties.rfh2Header.folders;
      folders.push({ string: '<RFH2Folder></RFH2Folder>' });
      var successCallback = function() {
        $timeout(function() {
          $scope.textMessageTabs.activeIndex = folders.length;
        });
      };
      $scope.update(isValid, successCallback);
    };

    $scope.autoSaveRFH2Folder = function(isValid) {
      var selectedTab = $scope.textMessageTabs.activeIndex;
      var successCallback = function() {
        $timeout(function() {
          $scope.textMessageTabs.activeIndex = selectedTab;
        });
      };
      $scope.autoSave(isValid, successCallback);
    };

    $scope.deleteRFH2Folder = function(isValid) {
      var selectedTab = $scope.textMessageTabs.activeIndex;
      var folders = $scope.teststep.otherProperties.rfh2Header.folders;
      folders.splice($scope.textMessageTabs.activeIndex - 1, 1);
      $scope.textMessageTabs.activeIndex = selectedTab - 1;    //  change index immediately for better UX (no blink)
      var successCallback = function() {
        $timeout(function() {
          $scope.textMessageTabs.activeIndex = selectedTab - 1;   //  redo the change-index due to $scope.teststep being refreshed causing the RFH2 folder tabs being recreated and tabset index being set to 0
        });
      };
      $scope.update(isValid, successCallback);
    };

    $scope.uploadRequestFile = function(file) {
      if (file) {
        var url = 'api/testcases/' + $scope.teststep.testcaseId + '/teststeps/' + $scope.teststep.id + '/requestFile';
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

    $scope.downloadRequestFile = function() {
      var url = 'api/testcases/' + $scope.teststep.testcaseId + '/teststeps/' + $scope.teststep.id + '/requestFile';
      $window.open(url, '_blank', '');
    };
  }
]);
