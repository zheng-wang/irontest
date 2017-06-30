'use strict';

//  NOTICE:
//    The $scope here prototypically inherits from the $scope of TeststepsController.
//    ng-include also creates a scope.
angular.module('irontest').controller('MQTeststepController', ['$scope', 'IronTestUtils', '$timeout', '$http',
    'Upload', '$window', 'Teststeps',
  function($scope, IronTestUtils, $timeout, $http, Upload, $window, Teststeps) {
    var timer;
    $scope.steprun = {};
    $scope.textMessageActiveTabIndex = 0;

    var clearPreviousRunAndAssertionVerificationStatus = function() {
      if (timer) $timeout.cancel(timer);
      $scope.steprun = {};
      $scope.assertionVerificationResult = null;
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
      clearPreviousRunAndAssertionVerificationStatus();

      var teststep = $scope.teststep;

      // initialize new action
      if (teststep.action === 'Enqueue' || teststep.action === 'Publish') {
        if (!teststep.otherProperties.messageFrom) {
          teststep.otherProperties.messageFrom = 'Text';
        }
      }

      //  save test step
      $scope.update(isValid);
    };

    $scope.endpointInfoIncomplete = function() {
      var endpointOtherProperties = $scope.teststep.endpoint.otherProperties;
      if (endpointOtherProperties) {
        return !endpointOtherProperties.queueManagerName || !endpointOtherProperties.host ||
          !endpointOtherProperties.port || !endpointOtherProperties.svrConnChannelName;
      } else {
        return true;
      }
    }

    $scope.actionInfoIncomplete = function() {
      var teststep = $scope.teststep;
      if (!teststep.action) {
        return true;
      } else if (teststep.otherProperties.destinationType === 'Queue') {
        return !teststep.otherProperties.queueName || (
          teststep.action === 'Enqueue' && (
            teststep.otherProperties.messageFrom === 'Text' && !teststep.request ||
            teststep.otherProperties.messageFrom === 'File' && !teststep.otherProperties.messageFilename
          )
        );
      } else if (teststep.otherProperties.destinationType === 'Topic') {
        return !teststep.otherProperties.topicString;
      } else {
        return true;
      }
    }

    $scope.doAction = function() {
      clearPreviousRunAndAssertionVerificationStatus();

      var teststep = new Teststeps($scope.teststep);
      $scope.steprun.status = 'ongoing';
      teststep.$run(function(basicTeststepRun) {
        $scope.steprun.response = basicTeststepRun.response.value;
        $scope.steprun.status = 'finished';
        timer = $timeout(function() {
          $scope.steprun.status = null;
        }, 15000);
      }, function(error) {
        $scope.steprun.status = 'failed';
        IronTestUtils.openErrorHTTPResponseModal(error);
      });
    };

    $scope.toggleRFH2Header = function(isValid) {
      var header = $scope.teststep.otherProperties.rfh2Header;
      if (header.enabled === true && header.folders.length === 0) {
        $scope.addRFH2Folder(isValid);
      } else {
        $scope.textMessageTabSelected(0);
        $scope.update(isValid);
      }
    };

    $scope.addRFH2Folder = function(isValid) {
      var folders = $scope.teststep.otherProperties.rfh2Header.folders;
      folders.push({ string: '<RFH2Folder></RFH2Folder>' });
      $scope.textMessageActiveTabIndex = folders.length;
      $scope.update(isValid);
    };

    $scope.textMessageTabSelected = function(index) {
      $scope.textMessageActiveTabIndex = index;
    };

    $scope.deleteRFH2Folder = function(isValid) {
      var folders = $scope.teststep.otherProperties.rfh2Header.folders;
      folders.splice($scope.textMessageActiveTabIndex - 1, 1);
      $scope.textMessageActiveTabIndex = $scope.textMessageActiveTabIndex - 1;
      $scope.update(isValid);
    };

    $scope.uploadRequestFile = function(file) {
      if (file) {
        var url = 'api/testcases/' + $scope.teststep.testcaseId + '/teststeps/' + $scope.teststep.id +
          '/uploadRequestFile';
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
      var url = 'api/testcases/' + $scope.teststep.testcaseId + '/teststeps/' + $scope.teststep.id +
        '/downloadRequestFile';
      $window.open(url, '_blank', '');
    };
  }
]);
